package seedu.address.model.statistics;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class StatisticsGeneratorFactoryTest {

    @Test
    public void constructor_nullBarChart_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new StatisticsGeneratorFactory(null));
    }

    @Test
    public void constructor_defaultFactory_createsGenerators() {
        StatisticsGeneratorFactory factory = new StatisticsGeneratorFactory();
        assertTrue(factory.create(StatisticsCategory.ROLE) instanceof RoleStatisticsGenerator);
        assertTrue(factory.create(StatisticsCategory.RECORD) instanceof RecordStatisticsGenerator);
    }

    @Test
    public void create_nullCategory_throwsNullPointerException() {
        StatisticsGeneratorFactory factory = new StatisticsGeneratorFactory(new AsciiBarChart(10));
        assertThrows(NullPointerException.class, () -> factory.create(null));
    }

    @Test
    public void create_roleCategory_returnsRoleGenerator() {
        StatisticsGeneratorFactory factory = new StatisticsGeneratorFactory(new AsciiBarChart(10));
        assertTrue(factory.create(StatisticsCategory.ROLE) instanceof RoleStatisticsGenerator);
    }

    @Test
    public void create_recordCategory_returnsRecordGenerator() {
        StatisticsGeneratorFactory factory = new StatisticsGeneratorFactory(new AsciiBarChart(10));
        assertTrue(factory.create(StatisticsCategory.RECORD) instanceof RecordStatisticsGenerator);
    }
}
