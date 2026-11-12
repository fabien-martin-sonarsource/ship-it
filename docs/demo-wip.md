echo '{}' | sonar hook agent-session-start --agent claude \
| jq -r '.hookSpecificOutput.additionalContext'