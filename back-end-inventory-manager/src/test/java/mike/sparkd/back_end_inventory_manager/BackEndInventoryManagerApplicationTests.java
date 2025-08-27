package mike.sparkd.back_end_inventory_manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest
class BackEndInventoryManagerApplicationTests {
    @Autowired
    private ApplicationContext ctx;

    @Test
    void contextLoads() {
        Assertions.assertNotNull(this.ctx);
    }
}
