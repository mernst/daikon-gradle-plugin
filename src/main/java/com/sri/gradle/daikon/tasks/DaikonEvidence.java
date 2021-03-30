package com.sri.gradle.daikon.tasks;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sri.gradle.daikon.Constants;
import com.sri.gradle.daikon.internal.CsvWriter;
import com.sri.gradle.daikon.utils.Filefinder;
import com.sri.gradle.daikon.utils.ImmutableStream;
import com.sri.gradle.daikon.utils.JavaProjectHelper;
import com.sri.gradle.daikon.utils.MoreFiles;
import org.gradle.api.GradleException;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.*;

@SuppressWarnings("UnstableApiUsage")
public class DaikonEvidence extends AbstractNamedTask {
  private static final String METRICS = "DaikonInvsAndMetrics";
  private static final String CONFIG = "DaikonPluginConfig";
  private static final String QUALIFICATION = "DaikonPluginQualification";

  private static final Map<String, CsvProcessor> CSVPROCESSORS = ImmutableMap.of(
      METRICS, new MetricsProcessor(),
      CONFIG, new ConfigProcessor(),
      QUALIFICATION, new QualificationProcessor()
  );

  private final DirectoryProperty outputDir;
  private final Property<String> testDriverPackage;

  public DaikonEvidence() {
    this.outputDir = getProject().getObjects().directoryProperty(); // unchecked warning
    this.testDriverPackage = getProject().getObjects().property(String.class); // unchecked warning
  }

  @TaskAction
  public void daikonEvidence() {
    // main data structure
    final Map<String, Map<String, Object>> evidence = new HashMap<>();
    initEvidence(evidence);


    // qualification data
    final Map<String, Object> qualification = new HashMap<>();
    qualification.put("TITLE", "DaikonGradlePlugin");
    qualification.put("SUMMARY", "Runs the Daikon Tool");
    qualification.put("QUALIFIEDBY", "SRI International");
    qualification.put("USERGUIDE", "https://github.com/SRI-CSL/daikon-gradle-plugin/blob/master/README.md");
    qualification.put("INSTALLATION", "https://github.com/SRI-CSL/daikon-gradle-plugin/blob/master/README.md");
    qualification.put("ACTIVITY", "Dynamic Analysis");

    final Map<String, Object> metrics = new HashMap<>();
    final Map<String, Object> config = new HashMap<>();

    final File daikonOutputDir = getOutputDir().getAsFile().get();
    final List<File> allTxtFiles = Filefinder.findTextFiles(daikonOutputDir.toPath());
    Optional<Path> invsFile =
        allTxtFiles.stream()
            .map(File::toPath)
            .filter(Files::exists)
            .filter(f -> f.getFileName().toString().endsWith("inv.txt"))
            .findAny();

    if (!invsFile.isPresent()) {
      getLogger().warn("Skipping evidence file generation. Unable to find .inv.txt file");
      return;
    }

    final Path workingDir = getProject().getProjectDir().toPath();
    config.put("OUTPUT_DIR", workingDir.relativize(daikonOutputDir.toPath()).toString());

    final Path actualInvsFile = invsFile.get();
    final Path daikonEvidenceFile = workingDir.resolve(Constants.DAIKON_DETAILS_FILE_NAME);

    try {
      Files.deleteIfExists(daikonEvidenceFile);
    } catch (IOException e) {
      throw new GradleException(String.format("Unable to delete %s", daikonEvidenceFile), e);
    }

    final String matchKey = getTestDriverPackage().get();

    final List<String> daikonOutFiles = ImmutableStream.listCopyOf(Filefinder.findAnyFiles(
            daikonOutputDir.toPath(), "inv.txt").stream()
            .map(f -> workingDir.relativize(f.toPath()).toString()));


    config.put("TEST_DRIVER_PACKAGE", matchKey);

    metrics.put("SUPPORT_FILES", daikonOutFiles);
    metrics.put("CORES", String.valueOf(Runtime.getRuntime().availableProcessors()));

    // This will return Long.MAX_VALUE if there is no preset limit
    long maxMemory = Runtime.getRuntime().maxMemory();
    metrics.put(
        "JVM_MEMORY_LIMIT_IN_BYTES",
        (maxMemory == Long.MAX_VALUE ? "no limit" : String.valueOf(maxMemory)));
    metrics.put(
        "MEMORY_AVAILABLE_TO_JVM_IN_BYTES", String.valueOf(Runtime.getRuntime().totalMemory()));

    // Invariants file
    metrics.put("INVARIANTS_FILE", workingDir.relativize(actualInvsFile).toString());

    final JavaProjectHelper helper = new JavaProjectHelper(getProject());
    final Path driverDir = helper.getDriverDir().toPath();
    final String mainClass = helper.findDriverClass().orElse(null);
    // Writing the test driver field depends on whether Daikon or Randoop has generated
    // the driver. Checking the latter is trickier as you have the option to place these
    // anywhere you want to, via the 'junitOutputDir' field. The former is easier for we
    // control where the driver should be placed. Having said, we check the former first.
    if (Constants.TEST_DRIVER_CLASSNAME.equals(mainClass)) {
      metrics.put(
          "TEST_DRIVER",
          workingDir
              .relativize(driverDir.resolve(Constants.TEST_DRIVER_CLASSNAME + ".java"))
              .toString());
    } else {
      // Search for the test files generated by Randoop
      final Path testClassesDir = helper.getTestMainDir().getAsFile().toPath();
      final Set<File> randoopGeneratedTests =
          MoreFiles.getMatchingJavaFiles(
              testClassesDir, Constants.EXPECTED_RANDOOP_TEST_NAME_REGEX);

      randoopGeneratedTests.stream()
          .filter(
              f ->
                  com.google.common.io.Files.getNameWithoutExtension(f.getName())
                      .endsWith(Constants.TEST_DRIVER))
          .findFirst()
          .ifPresent(
              testDriverJavaFile ->
                      metrics.put(
                      "TEST_DRIVER", workingDir.relativize(testDriverJavaFile.toPath()).toString()));
    }

    final LocalDate dateNow = LocalDate.now();
    qualification.put("DATE", String.format("%d-%d-%d", dateNow.getYear(), dateNow.getMonthValue(), dateNow.getDayOfMonth()));

    final ReadWriteDaikonDetails evidenceWriter =
        new ReadWriteDaikonDetails(actualInvsFile, daikonEvidenceFile, matchKey);

    try {
      metrics.putAll(evidenceWriter.processLineByLine());

      evidence.put(METRICS, metrics);
      evidence.put(QUALIFICATION, qualification);
      evidence.put(CONFIG, config);

      evidenceWriter.writeToJson(evidence);
      evidenceWriter.writeCsv(evidence);
      getLogger().debug("Generated metric, tool configuration, and qualification data.");
    } catch (IOException ioe) {
      throw new GradleException("Unable to process " + actualInvsFile, ioe);
    }

    getLogger().quiet("Successfully generated evidence file: " + daikonEvidenceFile.getFileName());
  }

  private static void initEvidence(Map<String, Map<String, Object>> evidenceObjectMap){
    evidenceObjectMap.put(METRICS, new HashMap<>());
    evidenceObjectMap.put(CONFIG, new HashMap<>());
    evidenceObjectMap.put(QUALIFICATION, new HashMap<>());
  }

  @OutputDirectory
  public DirectoryProperty getOutputDir() {
    return this.outputDir;
  }

  @Input
  public Property<String> getTestDriverPackage() {
    return this.testDriverPackage;
  }

  @Override
  protected String getTaskName() {
    return Constants.DAIKON_EVIDENCE_TASK;
  }

  @Override
  protected String getTaskDescription() {
    return Constants.DAIKON_EVIDENCE_TASK_DESCRIPTION;
  }

  static class ReadWriteDaikonDetails {
    private final Path inputFile;
    private final Path outFile;
    private final String matchKey;

    ReadWriteDaikonDetails(Path inputFile, Path outFile, String matchKey) {
      this.inputFile = Objects.requireNonNull(inputFile);
      this.outFile = outFile;
      this.matchKey = matchKey;
    }

    Map<String, String> processLineByLine() throws IOException {
      Preconditions.checkArgument(Files.exists(inputFile));

      final List<String> lines = new LinkedList<>();
      try (Scanner scanner = new Scanner(inputFile, Constants.ENCODING.name())) {
        while (scanner.hasNextLine()) {
          lines.add(scanner.nextLine());
        }
      }

      int idx = 0;
      final Set<String> testsExplored = new HashSet<>();
      final Set<String> classesExplored = new HashSet<>();
      final List<String> invDetected = new LinkedList<>();
      while (idx < lines.size()) {
        if (lines.get(idx).contains(Constants.DAIKON_SPLITTER)) {
          idx++;
          String ppName = lines.get(idx);
          // search for class name
          if (ppName.contains(":::")) {
            String target = ppName.substring(0, ppName.lastIndexOf(":::"));
            if (target.contains("(")) {
              target = target.substring(0, target.indexOf("("));
              target = target.substring(0, target.lastIndexOf("."));
            }

            if (Constants.EXPECTED_JUNIT4_NAME_REGEX.asPredicate().test(target)) {
              final String unitTest = ppName.substring(0, ppName.lastIndexOf(":::"));
              testsExplored.add(unitTest);
            } else if (!"org.junit.Assert".equals(target)) {
              classesExplored.add(target);
            }
          }
          idx++;
          // search for invariants associated with ppName
          while (idx < lines.size() && !lines.get(idx).contains(Constants.DAIKON_SPLITTER)) {
            if (lines.get(idx).startsWith(matchKey) || lines.get(idx).startsWith("this.")) {
              final String className = lines.get(idx).substring(0, lines.get(idx).lastIndexOf("."));
              if (!Constants.EXPECTED_JUNIT4_NAME_REGEX.asPredicate().test(className)) {
                invDetected.add(lines.get(idx));
              }
            }

            idx++;
          }
        } else {
          idx++;
        }
      }

      final Map<String, String> details = new IdentityHashMap<>();
      details.put("TESTS_COUNT", String.valueOf(testsExplored.size()));
      details.put("CLASSES_COUNT", String.valueOf(classesExplored.size()));
      details.put("INVARIANT_COUNT", String.valueOf(invDetected.size()));
      details.put("PP_COUNT", String.valueOf(testsExplored.size() + classesExplored.size()));

      return ImmutableMap.copyOf(details);
    }

    void writeToJson(Map<String, Map<String, Object>> evidence) throws IOException {

      final Map<String, Map<String, Map<String, Object>>> jsonDoc = new HashMap<>();
      jsonDoc.put("Evidence", evidence);

      Files.deleteIfExists(outFile);

      final Gson gson = new GsonBuilder().setPrettyPrinting().create();
      try (Writer writer = Files.newBufferedWriter(outFile)) {
        gson.toJson(jsonDoc, writer);
      }
    }

    void writeCsv(Map<String, Map<String, Object>> evidence){
      final Path workingDir = outFile.getParent();
      for (String each : evidence.keySet()){
        final Map<String, Object> recordMap = evidence.get(each);
        final CsvProcessor processor = CSVPROCESSORS.getOrDefault(each, null);
        if (processor == null) continue;
        processor.process(recordMap, workingDir);
      }
    }
  }

  public static class Record {
    String key;
    Object val;
    Record(String key, Object val){
      this.key = key;
      this.val = val;
    }

    public String getKey() {
      return key;
    }

    public Object getVal() {
      return val;
    }

    @Override
    public String toString() {
      return key + ":" + val.toString();
    }
  }

  interface CsvProcessor {
    void process(Map<String, Object> record, Path outputDir);
    default Set<Record> recordSet(Map<String, Object> record){
      final Set<Record> records = new HashSet<>();
      for (String each : record.keySet()){
        Object value = record.get(each);
        final Record r = new Record(each, value);
        records.add(r);
      }
      return records;
    }
  }

  static class MetricsProcessor implements CsvProcessor {
    @Override
    public void process(Map<String, Object> record, Path outputDir) {
      final StringBuilder sb = new StringBuilder();
      CsvWriter.writeCsv(outputDir, "DaikonInvsAndMetrics.csv", sb, recordSet(record));
    }
  }

  static class ConfigProcessor implements CsvProcessor {
    @Override
    public void process(Map<String, Object> record, Path outputDir) {
      final StringBuilder sb = new StringBuilder();
      CsvWriter.writeCsv(outputDir, "DaikonPluginConfig.csv", sb, recordSet(record));
    }
  }

  static class QualificationProcessor implements CsvProcessor {
    @Override
    public void process(Map<String, Object> record, Path outputDir) {
      final StringBuilder sb = new StringBuilder();
      CsvWriter.writeCsv(outputDir, "DaikonPluginQualification.csv", sb, recordSet(record));
    }
  }
}
