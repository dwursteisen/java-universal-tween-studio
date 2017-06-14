package aurelienribon.tweenstudio;

import aurelienribon.tweenengine.*;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com
 */
class ImportExportHelper {
    public static Timeline stringToDummyTimeline(String str) {
        Timeline tl = Timeline.createParallel();
        String[] lines = str.split("\n");

        // lire propriete forme ici
        Arrays.stream(lines)
                .map(line -> line.split(";"))
                .filter(parts -> parts.length >= 7)
                .map(parts -> {
                    try {
                        String targetName = parts[0];
                        Class targetClass = null;
                        targetClass = Class.forName(parts[1]);

                        int tweenType = Integer.parseInt(parts[2]);
                        int delay = Integer.parseInt(parts[3]);
                        int duration = Integer.parseInt(parts[4]);
                        TweenEquation equation = TweenUtils.parseEasing(parts[5]);

                        float[] targets = new float[parts.length - 6];
                        for (int i = 0; i < targets.length; i++)
                            targets[i] = Float.parseFloat(parts[i + 6]);

                        return Tween.to(null, tweenType, duration)
                                .cast(targetClass)
                                .target(targets)
                                .ease(equation)
                                .delay(delay)
                                .setUserData(targetName);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }).forEach(tl::push);

        return tl;
    }

    public static String timelineToString(Timeline timeline, Map<Object, String> targetsNamesMap) {
        StringBuilder str = new StringBuilder();

        // TODO: ajouter propriete des formes ici
        for (BaseTween child : timeline.getChildren()) {
            Tween tween = (Tween) child;

            str.append(String.format(Locale.US, "%s;%s;%d;%d;%d;%s",
                    targetsNamesMap.get(tween.getTarget()),
                    tween.getTargetClass().getName(),
                    tween.getType(),
                    (int) tween.getDelay(),
                    (int) tween.getDuration(),
                    tween.getEasing().toString()));

            for (int i = 0; i < tween.getCombinedAttributesCount(); i++)
                str.append(String.format(Locale.US, ";%f", tween.getTargetValues()[i]));

            str.append("\n");
        }

        return str.toString();
    }
}
