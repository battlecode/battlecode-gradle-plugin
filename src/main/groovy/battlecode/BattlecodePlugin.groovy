package battlecode

import org.gradle.api.Project
import org.gradle.api.Plugin

class BattlecodePlugin implements Plugin<Project> {
    void apply(Project target) {
        println greeting()
    }

    def greeting() {
        return "Welcome to battlecode!"
    }
}

