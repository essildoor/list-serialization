import com.paprika.ListRand;
import com.paprika.ListUtils;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static org.testng.Assert.assertTrue;


public class ListRandTest {
    private static final int TESTS_COUNT = 50;
    private static final int MAX_LIST_SIZE = 5;

    private File testDir;

    @BeforeClass
    public void setUp() throws Exception {
        testDir = new File("test_files");
        testDir.mkdir();
    }

    @AfterClass
    public void tearDown() throws Exception {
        Files.walk(testDir.toPath(), FileVisitOption.FOLLOW_LINKS)
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
    }

    @Test(dataProvider = "testDataProvider")
    public void serializeDeserialize(ListRand initial) throws Exception {
        // given
        File tmpFile = new File("test_files/tmp_" + UUID.randomUUID().toString() + ".dat");

        // when
        try (FileOutputStream fos = new FileOutputStream(tmpFile)) {
            initial.serialize(fos);
        }

        ListRand actual = new ListRand();
        try (FileInputStream fis = new FileInputStream(tmpFile)) {
            actual.deserialize(fis);
        }

        // then
        System.out.println("initial list        : " + initial);
        System.out.println("deserialized list   : " + actual);

        assertTrue(ListUtils.equals(initial, actual));
    }

    @DataProvider(name = "testDataProvider")
    public static Object[][] testDataProvider() {
        Object[][] testData = new Object[TESTS_COUNT][];
        for (int i = 0; i < TESTS_COUNT; i++) {
            testData[i] = new Object[]{randomList(ThreadLocalRandom.current().nextInt(0, MAX_LIST_SIZE))};
        }
        return testData;
    }

    private static ListRand randomList(int listSize) {
        if (listSize == 0) return ListUtils.from();

        String[] data = new String[listSize];
        for (int i = 0; i < listSize; i ++) {
            data[i] = UUID.randomUUID().toString();
        }

        return ListUtils.from(data);
    }
}
