package ru.semenyutin.lab1;
import org.junit.jupiter.api.Test;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;

class ExpeditionTest {
    Expedition fixed(int... rolls){return new Expedition(new Random(){int i;public int nextInt(int bound){return rolls[i++];}});}
    @Test void cargoIsKeptSeparateUntilDocking(){
        var g=fixed(0,46);g.fly(Expedition.Route.ORBIT);g.fly(Expedition.Route.ORBIT);
        assertEquals(7,g.cargo());assertEquals(6,g.energy());assertEquals(7,g.dock());assertEquals(13,g.energy());assertEquals(0,g.cargo());
    }
    @Test void freeFlightIsConsumedAndStormClearsCargo(){
        var g=fixed(0,99,84);g.fly(Expedition.Route.ORBIT);g.fly(Expedition.Route.ORBIT);
        assertTrue(g.free());g.fly(Expedition.Route.RIFT);assertFalse(g.free());assertEquals(6,g.energy());assertEquals(0,g.cargo());
    }
    @Test void cannotFlyOnEmptyEnergy(){
        var g=fixed(65,65,65,65,65,65,65,65);for(int i=0;i<8;i++)g.fly(Expedition.Route.ORBIT);
        assertFalse(g.canFly());assertThrows(IllegalStateException.class,()->g.fly(Expedition.Route.ORBIT));
    }
}
