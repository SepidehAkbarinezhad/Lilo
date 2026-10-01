"""Guard new Task code against Clean Architecture dependency regressions."""
from pathlib import Path
import re

root = Path(__file__).resolve().parents[1]
source = root / 'composeApp/src/commonMain/kotlin/com/sepideh/lilo/task'
violations = []
for layer in ('domain', 'presentation'):
    for path in (source / layer).rglob('*.kt'):
        # The legacy Notes screen is explicitly outside this refactor.
        if 'note' in path.relative_to(source).parts:
            continue
        for line in path.read_text().splitlines():
            if not line.startswith('import '):
                continue
            if re.search(r'com\.sepideh\.lilo\..*\.data\.', line):
                violations.append((path, line))
            if layer == 'domain' and any(token in line for token in ('.presentation.', 'androidx.', 'org.jetbrains.compose.', '.core.service.')):
                violations.append((path, line))
for path, line in violations:
    print(f'{path.relative_to(root)}: {line}')
assert not violations, 'Task layer boundary violations found'
print('Task dependency boundaries passed')
