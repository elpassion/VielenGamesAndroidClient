package com.elpassion.vielengames.ui;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.elpassion.vielengames.R;
import com.elpassion.vielengames.data.Game;
import com.elpassion.vielengames.data.Player;
import com.elpassion.vielengames.data.kuridor.KuridorGame;
import com.elpassion.vielengames.utils.ViewUtils;

import java.util.ArrayList;
import java.util.List;

public final class GamesAdapter extends BaseAdapter {

    private Context context;
    private Player me;
    private final LayoutInflater inflater;
    private List<Game> games;

    public GamesAdapter(Context context, Player me) {
        this.context = context;
        this.me = me;
        this.inflater = LayoutInflater.from(context);
        this.games = new ArrayList<Game>();
    }

    @Override
    public int getCount() {
        return games.size();
    }

    @Override
    public Game getItem(int position) {
        return games.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.game_item, parent, false);
        }
        KuridorGame item = (KuridorGame) getItem(position);
        CharSequence text = formatPlayerNames(item);
        ViewUtils.setText(text, convertView, R.id.game_item_player_names);
        return convertView;
    }

    private CharSequence formatPlayerNames(KuridorGame item) {
        List<Player> players = item.getPlayers();
        Player team1Player = "team_1".equals(players.get(0).getTeam()) ? players.get(0) : players.get(1);
        Player team2Player = "team_2".equals(players.get(0).getTeam()) ? players.get(0) : players.get(1);
        String name1 = me.equals(team1Player) ? "You" : team1Player.getName();
        String name2 = me.equals(team2Player) ? "You" : team2Player.getName();
        SpannableString spannableString = new SpannableString(name1 + " vs " + name2);
        int greenTextColor = context.getResources().getColor(R.color.text_green_color);
        spannableString.setSpan(
                new ForegroundColorSpan(greenTextColor),
                name1.length(),
                name1.length() + 4,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    public void updateGames(List<Game> games) {
        this.games = new ArrayList<Game>(games);
        notifyDataSetChanged();
    }
}
