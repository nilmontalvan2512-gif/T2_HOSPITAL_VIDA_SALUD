package pe.edu.hospitalvidasalud.appointments;

import java.util.EnumSet;
import java.util.Set;

public enum AppointmentStatus {
    PENDING,
    CONFIRMED,
    ATTENDED,
    CANCELLED,
    ABSENT;

    public boolean canTransitionTo(AppointmentStatus target) {
        return TRANSITIONS.get(this).contains(target);
    }

    private static final java.util.Map<AppointmentStatus, Set<AppointmentStatus>> TRANSITIONS =
            java.util.Map.of(
                    PENDING, EnumSet.of(CONFIRMED, CANCELLED),
                    CONFIRMED, EnumSet.of(ATTENDED, CANCELLED, ABSENT),
                    ATTENDED, EnumSet.noneOf(AppointmentStatus.class),
                    CANCELLED, EnumSet.noneOf(AppointmentStatus.class),
                    ABSENT, EnumSet.noneOf(AppointmentStatus.class));
}
