# Git Commit Guidelines for CalTrackPro Android

This project follows conventional commit format for all commits.

## Commit Message Format

```
type(scope): description

[optional body]

[optional footer]
```

## Types

| Type | Description |
|------|-------------|
| `feat` | New feature |
| `fix` | Bug fix |
| `docs` | Documentation changes |
| `style` | Code style changes (formatting, no logic change) |
| `refactor` | Code refactoring |
| `test` | Adding or updating tests |
| `chore` | Maintenance tasks, dependencies |
| `perf` | Performance improvements |
| `ci` | CI/CD changes |
| `build` | Build system changes |

## Scopes (Android-Specific)

| Scope | Description |
|-------|-------------|
| `diary` | Food diary feature |
| `search` | Food search feature |
| `barcode` | Barcode scanning |
| `recipe` | Recipe management |
| `insights` | Nutrition insights/charts |
| `profile` | User profile |
| `voice` | Voice input |
| `fit` | Google Fit integration |
| `fasting` | Intermittent fasting |
| `widget` | Home screen widgets |
| `billing` | Google Play Billing |
| `premium` | Premium features |
| `ui` | UI/Compose components |
| `data` | Data layer (Room, API) |
| `di` | Dependency injection |
| `nav` | Navigation |
| `security` | Security features |
| `offline` | Offline mode/caching |

## Examples

```bash
feat(diary): add food entry list screen
fix(barcode): resolve camera permission crash
docs(readme): update setup instructions
refactor(data): migrate to DataStore from SharedPreferences
test(search): add unit tests for food search repository
chore(deps): update Compose to 1.6.0
perf(insights): optimize chart rendering
```

## Best Practices

1. **Keep commits atomic** - One logical change per commit
2. **Write clear descriptions** - Explain what, not how
3. **Reference issues** - Use `fixes #123` in footer when applicable
4. **Keep first line under 72 characters**
5. **Use imperative mood** - "add feature" not "added feature"
