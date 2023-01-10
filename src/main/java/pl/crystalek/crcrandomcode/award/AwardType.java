package pl.crystalek.crcrandomcode.award;

import pl.crystalek.crcapi.core.config.exception.ConfigLoadException;

public enum AwardType {
    MONEY, ITEM;

    public static AwardType getAwardType(final String awardName) throws ConfigLoadException {
        for (final AwardType awardType : values()) {
            if (!awardType.name().equalsIgnoreCase(awardName)) {
                continue;
            }

            return awardType;
        }

        throw new ConfigLoadException("Nie odnaleziono nagrody typu: " + awardName);
    }
}
