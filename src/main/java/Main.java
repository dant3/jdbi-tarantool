import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;

import lombok.Data;

public class Main {
  public static void main(String... args) {
    Jdbi jdbi = Jdbi.create("jdbc:tarantool://localhost:3301?user=guest");
    SampleData sampleData = jdbi.withHandle(Main::readSampleFromTarantool);

    System.out.println("Got sample data: " + sampleData);
  }

  private static SampleData readSampleFromTarantool(final Handle h) {
    h.execute("CREATE TABLE IF NOT EXISTS table1 (column1 INTEGER PRIMARY KEY, column2 VARCHAR(100));");
    h.execute("INSERT INTO table1 VALUES (1, 'A');");
    h.execute("UPDATE table1 SET column2 = 'B';");
    return h.createQuery("SELECT * FROM table1 WHERE column1 = 1;")
            .mapToBean(SampleData.class)
            .stream()
            .findFirst()
            .orElseThrow(IllegalStateException::new);
  }

  @Data
  public static class SampleData {
    private int column1;
    private String column2;
  }
}
