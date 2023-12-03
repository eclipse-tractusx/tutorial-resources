package com.eclipse.mxd;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;

@ApplicationScoped
public class AppInitializer {
    public void onApplicationStart(@Observes @Initialized(ApplicationScoped.class) Object init) {

    }
}
