package battlecode

import org.gradle.api.Project
import org.gradle.api.Plugin
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.JavaExec

class BattlecodePlugin implements Plugin<Project> {
    void apply(Project project) {
        println greeting()

        project.task('runFromClient', type: JavaExec, dependsOn: 'build') {
            main = 'battlecode.server.Main'
            classpath = project.sourceSets.main.runtimeClasspath
            args = ['-c=-']
            jvmArgs = [
                '-Dbc.server.wait-for-client=true',
                '-Dbc.server.mode=headless',
                '-Dbc.server.map-path=maps',
                '-Dbc.server.debug=false',
                '-Dbc.server.robot-player-to-system-out=false',
                '-Dbc.engine.debug-methods=true',
                '-Dbc.game.team-a='+project.findProperty('teamA'),
                '-Dbc.game.team-b='+project.findProperty('teamB'),
                '-Dbc.game.team-a.url='+project.buildDir+'/classes',
                '-Dbc.game.team-b.url='+project.buildDir+'/classes',
                '-Dbc.game.maps='+project.findProperty('maps'),
                '-Dbc.server.save-file=' + 'matches/' + project.findProperty('teamA') + '-vs-' + project.findProperty('teamB') + '-on-' + project.findProperty('maps') + '.bc17'
            ]
        }

        project.task('runDebug', type: JavaExec, dependsOn: 'build') {
            main = 'battlecode.server.Main'
            classpath = project.sourceSets.main.runtimeClasspath
            args = ['-c=-']
            jvmArgs = [
                '-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=5005',
                '-Dbc.server.mode=headless',
                '-Dbc.server.map-path=maps',
                '-Dbc.server.debug=false',
                '-Dbc.server.robot-player-to-system-out=false',
                '-Dbc.engine.debug-methods=true',
                '-Dbc.game.team-a='+project.findProperty('teamA'),
                '-Dbc.game.team-b='+project.findProperty('teamB'),
                '-Dbc.game.team-a.url='+project.buildDir+'/classes',
                '-Dbc.game.team-b.url='+project.buildDir+'/classes',
                '-Dbc.game.maps='+project.findProperty('maps'),
                '-Dbc.server.save-file=' + 'matches/' + project.findProperty('teamA') + '-vs-' + project.findProperty('teamB') + '-on-' + project.findProperty('maps') + '.bc17'
            ]
        }

        def arch64 = false
        def arch32 = false

        if (System.getProperty("os.arch").matches("^(x8664|amd64|ia32e|em64t|x64)\$")) {
            arch64 = true
        }
        if (System.getProperty("os.arch").matches("^(x8632|x86|i[3-6]86|ia32|x32)\$")) {
            arch32 = true
        }

        project.configurations {
            client32
        }

        project.repositories {
            maven {
                url "http://battlecode-maven.s3-website-us-east-1.amazonaws.com/"
            }
        }

        if (arch32) {
            def os = System.getProperty("os.name").toLowerCase()
            def clientName = os.startsWith('windows') ? 'battlecode-client-win-32' :
                             os.startsWith('mac') ? 'UNSUPPORTED' :
                             'battlecode-client-linux-32'

            if (clientName.equals('UNSUPPORTED')) {
                println 'Sorry, the Battlecode client does not support 32-bit architectures for OS X.'
                project.unpackClient32.onlyIf { false }
            }

            project.dependencies {
                client32 group: 'org.battlecode', name: clientName, version: '2017.+'
            }
        }

        project.task('unpackClient32', type: Copy, dependsOn: project.configurations.client32) {
            description 'Downloads the 32-bit client.'
            group 'battlecode'

            dependsOn project.configurations.client32

            from {
                project.configurations.client32.collect {
                    project.zipTree(it)
                }
            }
            into 'client32/'
        }

        project.unpackClient32.onlyIf { arch32 }
        project.build.dependsOn('unpackClient32')
    }

    def greeting() {
        return "Welcome to battlecode!"
    }
}

