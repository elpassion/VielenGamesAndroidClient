package com.vielengames.utils.kuridor;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.vielengames.data.kuridor.KuridorGameState;
import com.vielengames.data.kuridor.KuridorMove;

import java.util.Collection;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

public final class KuridorGameStateDrawer {

    private KuridorGameStateDrawer() {
    }

    public static void draw(KuridorGameState state, Canvas canvas, Settings settings) {
        int size = Math.min(settings.width(), settings.height());
        float xPadding = settings.padding() + (settings.width() - size) / 2.0f;
        float yPadding = settings.padding() + (settings.height() - size) / 2.0f;
        for (int y = 0; y < 10; y++) {
            if (y == 0) {
                settings.paint().setColor(settings.team1Color());
            } else if (y == 9) {
                settings.paint().setColor(settings.team2Color());
            } else {
                settings.paint().setColor(0xFF000000);
            }
            for (int x = 0; x < 10; x++) {
                canvas.drawCircle(
                        xPadding + (size - 1 - 2 * settings.padding()) * x / 9.0f,
                        yPadding + (size - 1 - 2 * settings.padding()) * y / 9.0f,
                        y != 0 && y != 9
                                ? settings.dotsRadius()
                                : settings.lastLineDotsRadius(),
                        settings.paint());
            }
        }
        settings.paint().setColor(0xFF000000);
        settings.paint().setStrokeWidth(settings.wallWidth());
        for (String wall : state.getWalls()) {
            int centerX = wall.charAt(0) - 'a' + 1;
            int centerY = '8' - wall.charAt(1) + 1;
            int startX, startY, stopX, stopY;
            float wallPaddingX = 0.0f, wallPaddingY = 0.0f;
            if (wall.charAt(2) == 'h') {
                startX = centerX - 1;
                startY = centerY;
                stopX = centerX + 1;
                stopY = centerY;
                wallPaddingX = settings.wallPadding();
            } else {
                startX = centerX;
                startY = centerY - 1;
                stopX = centerX;
                stopY = centerY + 1;
                wallPaddingY = settings.wallPadding();
            }
            canvas.drawLine(
                    xPadding + (size - 1 - 2 * settings.padding()) * startX / 9.0f + wallPaddingX,
                    yPadding + (size - 1 - 2 * settings.padding()) * startY / 9.0f + wallPaddingY,
                    xPadding + (size - 1 - 2 * settings.padding()) * stopX / 9.0f - wallPaddingX,
                    yPadding + (size - 1 - 2 * settings.padding()) * stopY / 9.0f - wallPaddingY,
                    settings.paint());
        }
        settings.paint().setColor(settings.team1Color());
        String team1PawnPosition = state.getTeam1().getPawnPosition();
        int xTeam1 = 1 + 2 * (team1PawnPosition.charAt(0) - 'a');
        int yTeam1 = 1 + 2 * ('9' - team1PawnPosition.charAt(1));
        canvas.drawCircle(
                xPadding + (size - 1 - 2 * settings.padding()) * xTeam1 / 18.0f,
                yPadding + (size - 1 - 2 * settings.padding()) * yTeam1 / 18.0f,
                (size - 1 - 2 * settings.padding()) / 18.0f - settings.pawnPadding(),
                settings.paint());
        settings.paint().setColor(settings.team2Color());
        String team2PawnPosition = state.getTeam2().getPawnPosition();
        int xTeam2 = 1 + 2 * (team2PawnPosition.charAt(0) - 'a');
        int yTeam2 = 1 + 2 * ('9' - team2PawnPosition.charAt(1));
        canvas.drawCircle(
                xPadding + (size - 1 - 2 * settings.padding()) * xTeam2 / 18.0f,
                yPadding + (size - 1 - 2 * settings.padding()) * yTeam2 / 18.0f,
                (size - 1 - 2 * settings.padding()) / 18.0f - settings.pawnPadding(),
                settings.paint());
        if (settings.drawLegalPawnMoves()) {
            int activeTeamColor = "team_1".equals(state.getActiveTeam()) ? settings.team1Color() : settings.team2Color();
            activeTeamColor &= 0x80FFFFFF;
            settings.paint().setColor(activeTeamColor);
            String pawnPosition = state.getActiveTeamPawnPosition();
            Collection<KuridorMove> legalMoves = state.getLegalPawnMoves();
            for (KuridorMove move : legalMoves) {
                int xActive = 1 + 2 * (pawnPosition.charAt(0) - 'a');
                int yActive = 1 + 2 * ('9' - pawnPosition.charAt(1));
                int xDestination = 1 + 2 * (move.getPosition().charAt(0) - 'a');
                int yDestination = 1 + 2 * ('9' - move.getPosition().charAt(1));
                canvas.drawLine(
                        xPadding + (size - 1 - 2 * settings.padding()) * xActive / 18.0f,
                        yPadding + (size - 1 - 2 * settings.padding()) * yActive / 18.0f,
                        xPadding + (size - 1 - 2 * settings.padding()) * xDestination / 18.0f,
                        yPadding + (size - 1 - 2 * settings.padding()) * yDestination / 18.0f,
                        settings.paint());
            }
        }
    }

    @Accessors(fluent = true)
    @Getter
    @Setter
    public static final class Settings {

        private int width;
        private int height;
        private Paint paint;
        private float padding;
        private float dotsRadius;
        private float lastLineDotsRadius;
        private float wallWidth;
        private float wallPadding;
        private float pawnPadding;
        private int team1Color;
        private int team2Color;
        private boolean drawLegalPawnMoves;
    }
}
