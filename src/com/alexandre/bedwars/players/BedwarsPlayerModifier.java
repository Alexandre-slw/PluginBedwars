package com.alexandre.bedwars.players;

import com.alexandre.core.players.GamePlayer;
import com.alexandre.core.players.GamePlayerModifier;

public class BedwarsPlayerModifier extends GamePlayerModifier {

    @Override
    public GamePlayer onAddPlayer(GamePlayer player) {
        return new BedwarsPlayer(player);
    }

    @Override
    public void onRemovePlayer(GamePlayer player) {

    }
}
