import com.paprika.ListRand;
import com.paprika.ListUtils;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.UUID;

import static org.testng.Assert.assertTrue;


public class ListRandTest {

    @Test(dataProvider = "testDataProvider")
    public void serializeDeserialize(ListRand initial) throws Exception {
        // given
        File tmpFile = new File("tmp_" + UUID.randomUUID().toString() + ".dat");

        // when
        try (FileOutputStream fos = new FileOutputStream(tmpFile)) {
            initial.serialize(fos);
        }

        ListRand actual = new ListRand();
        try (FileInputStream fis = new FileInputStream(tmpFile)) {
            actual.deserialize(fis);
        } finally {
            tmpFile.delete();
        }

        // then
        assertTrue(ListUtils.equals(initial, actual));
    }

    @DataProvider(name = "testDataProvider")
    public static Object[][] testDataProvider() {
        return new Object[][]{
                {ListUtils.from()},
                {ListUtils.from("1")},
                {ListUtils.from("1", "2")},
                {ListUtils.from("1", "2", "3", "4", "5", "", "  ")},
        };
    }
}
