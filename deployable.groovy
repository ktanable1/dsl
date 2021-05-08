
def BRANCHES=[
"master",
"feature/development",
...
]

def DEPLOYABLES=[
"deployable_pipeline" : ["pipeline/deployed_app.jar", ["DEV","QA","UAT"]],
...
]


DEPLOYABLES.each {
    String name="${it.key}"
    String script = "${it.value[0]}"
    List envs = it.value[1]

    pipelineJob("Commerce_pipeline/${name}") {
        description()
        keepDependencies(false)

        parameters {
            labelParam('node') {
                defaultValue('Commerce')
                description('Select nodes')
                allNodes('allCases', 'IgnoreOfflineNodeEligibility')
            }
            choiceParam("BRANCH", BRANCHES, "")
            choiceParam("ENV", envs, "")
        }
        definition {
            cpsScm {
                lightweight(true)
                scm {
                    git {
                        remote { 
                        url("${GIT_URL}")
                        credentials("${GIT_CREDENTIAL_ID}")
                        }
                        branch("${GIT_BRANCH}")
                    }
                }
                scriptPath(script)
            }
        }
        disabled(false)
        configure {
            it / 'properties' / 'com.sonyericsson.rebuild.RebuildSettings' {
                'autoRebuild'('false')
                'rebuildDisabled'('false')
            }
            it / 'properties' / 'jenkins.model.BuildDiscarderProperty' {
                strategy {
                    'daysToKeep'('-1')
                    'numToKeep'('15')
                    'artifactDaysToKeep'('-1')
                    'artifactNumToKeep'('-1')
                }
            }
        }
    }
}

