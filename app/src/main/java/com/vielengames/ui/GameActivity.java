package com.vielengames.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.vielengames.ForegroundNotifier;
import com.vielengames.R;
import com.vielengames.VielenGamesPrefs;
import com.vielengames.api.VielenGamesClient;
import com.vielengames.data.Game;
import com.vielengames.data.Player;
import com.vielengames.data.VielenGamesModel;
import com.vielengames.data.kuridor.KuridorGame;
import com.vielengames.data.kuridor.KuridorGameState;
import com.vielengames.data.kuridor.KuridorMove;
import com.vielengames.event.GamesUpdatedEvent;
import com.vielengames.event.MoveFailureEvent;
import com.vielengames.event.bus.EventBus;
import com.vielengames.ui.kuridor.GameHeaderLayout;
import com.vielengames.ui.kuridor.GameView;
import com.vielengames.utils.ViewUtils;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import hrisey.InstanceState;

public class GameActivity extends BaseActivity {

    private static final String EXTRA_GAME = "game";

    @Inject
    VielenGamesClient gameClient;
    @Inject
    EventBus eventBus;
    @Inject
    ForegroundNotifier notifier;
    @Inject
    VielenGamesModel model;
    @Inject
    VielenGamesPrefs prefs;

    @InstanceState
    private KuridorGame game;

    private GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_activity);
        eventBus.register(this);

        gameView = (GameView) findViewById(R.id.game_view);
        gameView.setMoveListener(new GameView.MoveListener() {
            @Override
            public void onMove(KuridorMove move) {
                if (!imActiveUser()) {
                    Toast.makeText(GameActivity.this, "It's not your turn", Toast.LENGTH_SHORT).show();
                } else if (!game.getCurrentState().isMoveValid(move)) {
                    Toast.makeText(GameActivity.this, "Illegal move: " + move.getPosition(), Toast.LENGTH_SHORT).show();
                } else {
                    gameClient.move(game, move);
                }
            }
        });
        if (game == null) {
            game = getIntent().getParcelableExtra(EXTRA_GAME);
        }
        updateGameView(Collections.singletonList(game));
        showGameHelpFirstTime();
    }

    private void showGameHelpFirstTime() {
        if (!prefs.getHelpOverlayAlreadyShown()) {
            prefs.setHelpOverlayAlreadyShown(true);
            showGameHelp();
        }
    }

    private void showGameHelp() {
        Intent intent = new Intent(this, GameHelpOverlayActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.game, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.game_help:
                showGameHelp();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("unused")
    public void onEvent(MoveFailureEvent event) {
        switch (event.getReason()) {
            case ILLEGAL_MOVE:
                Toast.makeText(GameActivity.this, "Server says: Illegal move: " + event.getMove().getPosition(), Toast.LENGTH_SHORT).show();
                break;
            case NOT_YOUR_TURN:
                Toast.makeText(GameActivity.this, "Server says: It's not your turn", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private boolean imActiveUser() {
        return prefs.getMe().equals(game.getActivePlayer());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(EXTRA_GAME, game);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        eventBus.unregister(this);
    }

    @SuppressWarnings("unused")
    public void onEvent(GamesUpdatedEvent event) {
        updateGameView(event.getGames());
    }

    private void updateGameView(List<? extends Game> games) {
        if (!games.contains(game)) {
            return;
        }
        KuridorGame thisGame = model.getGameById(game.getId());
        if (thisGame != null) {
            game = thisGame;
        }
        KuridorGameState state = game.getCurrentState();
        gameView.setState(state);
        List<Player> players = game.getPlayers();
        Player team1Player = "team_1".equals(players.get(0).getTeam()) ? players.get(0) : players.get(1);
        Player team2Player = "team_2".equals(players.get(0).getTeam()) ? players.get(0) : players.get(1);
        GameHeaderLayout top = ViewUtils.findView(this, R.id.game_header_top);
        top.update(team2Player, state.getTeam2().getWallsLeft(), "team_2".equals(state.getActiveTeam()));
        GameHeaderLayout bottom = ViewUtils.findView(this, R.id.game_header_bottom);
        bottom.update(team1Player, state.getTeam1().getWallsLeft(), "team_1".equals(state.getActiveTeam()));

        if (game.getWinner() != null) {
            ResultOverlayActivity.intent(this)
                    .game(game)
                    .start();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        notifier.onActivityStarted();
    }

    @Override
    protected void onStop() {
        super.onStop();
        notifier.onActivityStopped();
    }

    public static IntentBuilder intent(Context context) {
        return new IntentBuilder(context);
    }

    public static class IntentBuilder {

        private final Context context;
        private KuridorGame game;

        IntentBuilder(Context context) {
            this.context = context;
        }

        public IntentBuilder game(KuridorGame game) {
            this.game = game;
            return this;
        }

        public void start() {
            Intent intent = new Intent(context, GameActivity.class);
            intent.putExtra(EXTRA_GAME, game);
            context.startActivity(intent);
        }
    }
}