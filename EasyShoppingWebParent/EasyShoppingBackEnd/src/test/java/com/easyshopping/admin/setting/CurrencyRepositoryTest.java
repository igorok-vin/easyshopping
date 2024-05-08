package com.easyshopping.admin.setting;

import com.easyshopping.admin.setting.general.repository.CurrencyRepository;
import com.easyshopping.common.entity.setting.general.Currency;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class CurrencyRepositoryTest {

    @Autowired
    private CurrencyRepository currencyRepository;

    @Test
    public void testCreateCurrencies() {
        List<Currency> currencyList = Arrays.asList(
                new Currency("United States Dollar", "$", "USD"),
                new Currency("British Pound", "?", "GPB"),
                new Currency("Japanese Yen", "?", "JPY"),
                new Currency("Euro", "ˆ", "EUR"),
                new Currency("South Korean Won", "?", "KRW"),
                new Currency("Chinese Yuan", "?", "CNY"),
                new Currency("Brazilian Real", "R$", "BRL"),
                new Currency("Australian Dollar", "$", "AUD"),
                new Currency("Canadian Dollar", "$", "CAD"),
                new Currency("Vietnamese ??ng ", "?", "VND"),
                new Currency("Indian Rupee", "?", "INR")
        );
        currencyRepository.saveAll(currencyList);

        Iterable<Currency> iterable = currencyRepository.findAll();

        assertThat(iterable).size().isGreaterThan(0);
    }

    @Test
    public void testFindAll1() {
        Iterable<Currency> currencyIterable = currencyRepository.findAll();
        currencyIterable.forEach(System.out::println);

        assertThat(currencyIterable).size().isGreaterThan(0);
    }

    @Test
    public void testFindAll2() {
        List<Currency> findAll = currencyRepository.findAllByNameInAscOrder();
        findAll.forEach(System.out::println);

        assertThat(findAll).size().isEqualTo(7);
    }
}
