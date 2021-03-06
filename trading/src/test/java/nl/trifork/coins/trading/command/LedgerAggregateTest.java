package nl.trifork.coins.trading.command;

import nl.trifork.coins.coreapi.CreateLedgerCommand;
import nl.trifork.coins.coreapi.LedgerCreatedEvent;
import nl.trifork.coins.coreapi.LedgerMutatedEvent;
import nl.trifork.coins.coreapi.MutateLedgerCommand;
import org.axonframework.test.aggregate.AggregateTestFixture;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class LedgerAggregateTest {

    private AggregateTestFixture<LedgerAggregate> fixture;

    @Before
    public void setUp() {
        fixture = new AggregateTestFixture<>(LedgerAggregate.class);
    }

    @Test
    public void createLedger() {
        Map<String, BigDecimal> assets = new HashMap<>();
        assets.put("EUR",new BigDecimal("10000"));
        fixture.givenNoPriorActivity()
                .when(new CreateLedgerCommand("test"))
                .expectEvents(new LedgerCreatedEvent("test",assets));
    }

    @Test
    public void executeMutation() {
        Map<String, BigDecimal> assets = new HashMap<>();
        assets.put("EUR",new BigDecimal("9999"));
        assets.put("BTC",new BigDecimal("10"));
        fixture.givenCommands(new CreateLedgerCommand("test"))
                .when(new MutateLedgerCommand("test","EUR",BigDecimal.ONE,"BTC",BigDecimal.TEN))
                .expectEvents(new LedgerMutatedEvent("test",assets));
    }
}
