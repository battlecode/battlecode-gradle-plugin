package battlecode

import org.gradle.api.Project
import org.gradle.api.Plugin
import org.gradle.api.tasks.JavaExec

class BattlecodePlugin implements Plugin<Project> {
    void apply(Project project) {
        println greeting()

        if (!project.hasProperty("teamA")) {
            project.ext.teamA = "examplefuncsplayer"
        }
        if (!project.hasProperty("teamB")) {
            project.ext.teamB = "examplefuncsplayer"
        }
        if (!project.hasProperty("maps")) {
            project.ext.maps = "shrine"
        }

        project.task('runFromClient', type: JavaExec, dependsOn: 'build') {
            main = 'battlecode.server.Main'
            classpath = project.sourceSets.main.runtimeClasspath
            args = ['-c=-']
            jvmArgs = [
                '-Dbc.server.wait-for-client=true',
                '-Dbc.server.mode=headless',
                '-Dbc.server.map-path=maps',
                '-Dbc.server.debug=true',
                '-Dbc.engine.debug-methods=true',
                '-Dbc.game.team-a='+project.property('teamA'),
                '-Dbc.game.team-b='+project.property('teamB'),
                '-Dbc.game.team-a.url='+project.buildDir+'/classes',
                '-Dbc.game.team-b.url='+project.buildDir+'/classes',
                '-Dbc.game.maps='+project.property('maps'),
                '-Dbc.server.save-file=' + 'matches/' + project.property('teamA') + '-vs-' + project.property('teamB') + '-on-' + project.property('maps') + '.bc17'
            ]
        }
    }

    def greeting() {
        return "Welcome to battlecode!"
    }
}

