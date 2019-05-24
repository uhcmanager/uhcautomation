package usa.cactuspuppy.uhc_automation.game;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import usa.cactuspuppy.uhc_automation.game.types.UHC;

import java.util.UUID;

import static org.junit.Assert.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(World.class)
@Ignore
public class GameManagerTest {
    private static World world = Mockito.mock(World.class);
    private static Player player = Mockito.mock(Player.class);
    private static UHC test;
    static {
        Mockito.when(world.getUID()).thenReturn(new UUID(0L, 1L));
        Mockito.when(player.getUniqueId()).thenReturn(new UUID(0L, 2L));
        test = new UHC(world);
    }

    @Before
    public void prep() {
        GameManager.reset();
    }

    @Test
    public void registerTest() {
        GameManager.registerGame(test);
        UUID id = test.getGameID();
        assertNotEquals(new UUID(0, 0), id);
        assertNotNull(GameManager.getGame(id));
    }

    @Test
    public void isNameActive() {
        UHC test1 = new UHC(world);
        String name = "Lookiecookie.43%";
        test1.setName(name);
        GameManager.registerGame(test1);
        assertTrue(GameManager.isNameActive(name));
        GameManager.unregisterGame(test1, true);
    }

    @Test
    public void registerPlayerGame() {

    }

    @Test
    public void registerWorldGame() {
    }

    @Test
    public void unregisterPlayerGame() {
    }

    @Test
    public void unregisterWorldGame() {
    }
}