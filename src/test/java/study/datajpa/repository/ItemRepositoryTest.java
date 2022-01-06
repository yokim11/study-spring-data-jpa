package study.datajpa.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import study.datajpa.domain.entity.Item;

@SpringBootTest
class ItemRepositoryTest {

    @Autowired
    ItemRepository itemRepository;

    @Test
    public void saveTest() {
        Item item = new Item();
        Item savedItem = itemRepository.save(item);
        Item savedItem2 = itemRepository.save(item);

        assertThat(item.getId()).isEqualTo(savedItem.getId());

    }
}