# AGENTS.md — Execution Protocol

## Overview

This repository uses spec-driven development. Features are implemented by following
pre-approved plans derived from specifications.

**Your role:** Execute tasks from the plan sequentially, validate each task, and
report progress at checkpoints.

---

## Directory Structure
```
/specs/{feature}/           # READ-ONLY — Do not modify during implementation
  requirements.md           # Business requirements and clarifications
  acceptance-criteria.md    # Behavioral specs (WHEN-THEN-SHALL)
  technical-spec.md         # Architecture, constraints, patterns
  plans/                    # Execution plan — Update status only
    plan.yaml                 # Phased plan with dependencies
    tasks/
      task-{phase}.{n}.md     # Individual task cards (e.g., task-1.1.md, task-1.2.md)
    status.md                 # Progress tracking (you update this)
/src/                       # Implementation code
```

---

## Task List Workflow

### Initialization

When starting work on a feature:
```
1. READ /specs/{feature}/technical-spec.md
   → Understand architecture and constraints

2. READ /specs/{feature}/acceptance-criteria.md
   → Understand expected behavior

3. READ /specs/{feature}/plans/plan.yaml
   → Understand phases, dependencies, checkpoints

4. READ /specs/{feature}/plans/status.md
   → Determine current position in plan
   → If file doesn't exist, create it with phase-1, task-1.1 as NOT_STARTED

5. SET current_task = first task with status NOT_STARTED
```

### Main Execution Loop
```
WHILE current_task EXISTS:

    1. LOAD task card from specs/{feature}/plans/tasks/task-{phase}.{n}.md
    
    2. CHECK prerequisites
       FOR each task_id in task.depends_on:
           IF status[task_id] != COMPLETE:
               HALT with error "Dependency {task_id} not complete"
    
    3. UPDATE status.md
       SET current_task.status = IN_PROGRESS
    
    4. EXECUTE task
       - Follow instructions in task card
       - Create/modify only files listed in task card
       - Apply constraints from technical-spec.md
    
    5. VALIDATE task
       FOR each criterion in task.acceptance:
           RUN validation check
           IF check FAILS:
               INCREMENT retry_count
               IF retry_count > 2:
                   SET current_task.status = BLOCKED
                   HALT with VALIDATION_FAILURE report
               ELSE:
                   GOTO step 4 (retry execution)
    
    6. COMPLETE task
       UPDATE status.md:
           SET current_task.status = COMPLETE
           ADD to completed_tasks table
    
    7. DETERMINE next action
       IF current_task is last task in phase:
           HALT with CHECKPOINT report
           WAIT for approval
           IF approved:
               SET current_task = first task of next phase
           ELSE IF revision requested:
               PROCESS revision instructions
               CONTINUE
       ELSE:
           SET current_task = next task in phase (task-{phase}.{n+1}.md)

END WHILE

GENERATE completion report
```

### Task Card Processing

Each task card (`task-{phase}.{n}.md`) contains sections that map to execution steps:
```
┌─────────────────────────────────────────────────────────────┐
│ task-{phase}.{n}.md                                         │
├─────────────────────────────────────────────────────────────┤
│ ## Context                                                  │
│ → Read for understanding, no action required                │
├─────────────────────────────────────────────────────────────┤
│ ## Prerequisites                                            │
│ → Verify ALL items before proceeding                        │
│ → If any prerequisite fails, HALT                           │
├─────────────────────────────────────────────────────────────┤
│ ## Instructions                                             │
│ → Execute steps in order                                    │
│ → Each step should produce a verifiable result              │
├─────────────────────────────────────────────────────────────┤
│ ## Constraints                                              │
│ → Apply during execution                                    │
│ → MUST = mandatory, violation requires rollback             │
│ → SHOULD = preference, deviation requires justification     │
│ → MUST NOT = prohibition, violation requires rollback       │
├─────────────────────────────────────────────────────────────┤
│ ## Files to Create/Modify                                   │
│ → Checklist of expected artifacts                           │
│ → Do not create files not in this list                      │
├─────────────────────────────────────────────────────────────┤
│ ## Acceptance Criteria                                      │
│ → Validation checklist, ALL must pass                       │
│ → Execute after implementation complete                     │
├─────────────────────────────────────────────────────────────┤
│ ## Rollback                                                 │
│ → Execute if task fails after retries                       │
│ → Restore to pre-task state before reporting blocker        │
└─────────────────────────────────────────────────────────────┘
```

### Phase Transitions
```
WHEN last task of phase completes:

    1. COLLECT phase summary
       - List all completed tasks
       - List all created files
       - Map to acceptance criteria from spec

    2. GENERATE checkpoint report (see format below)

    3. HALT execution

    4. WAIT for human response:
       
       IF "APPROVED":
           CONTINUE to next phase
       
       IF "APPROVED WITH NOTES: {notes}":
           RECORD notes in status.md
           CONTINUE to next phase
       
       IF "REVISE: task-{phase}.{n} - {instructions}":
           SET current_task = specified task
           SET current_task.status = IN_PROGRESS
           APPLY revision instructions
           RE-VALIDATE task
           RESUME from step 7 of main loop
       
       IF "BLOCKED: {reason}":
           RECORD blocker in status.md
           HALT until blocker resolved
```

---

## Status File Format

Maintain `/specs/{feature}/plans/status.md`:
```markdown
# Implementation Status: {feature}

## Current Position
- Phase: {phase-id}
- Task: task-{phase}.{n}
- Status: NOT_STARTED | IN_PROGRESS | BLOCKED | COMPLETE

## Progress

### Phase 1: {phase name}
| Task | Status | Notes |
|------|--------|-------|
| task-1.1 | ✓ COMPLETE | |
| task-1.2 | ✓ COMPLETE | |
| task-1.3 | → IN_PROGRESS | |
| task-1.4 | · NOT_STARTED | |

### Phase 2: {phase name}
| Task | Status | Notes |
|------|--------|-------|
| task-2.1 | · NOT_STARTED | |
| task-2.2 | · NOT_STARTED | |

## Checkpoints
| Phase | Status | Approved |
|-------|--------|----------|
| phase-1 | PENDING | |
| phase-2 | NOT_REACHED | |

## Blockers
<!-- empty if none -->

## Deviations
<!-- record any approved deviations from plan -->
```

---

## Checkpoint Report Format
```markdown
# Checkpoint: Phase {n} Complete

## Phase Summary
- Phase: {phase-id} — {phase name}
- Tasks completed: {n}/{n}

## Completed Tasks
| Task | Description |
|------|-------------|
| task-{phase}.1 | {task name} |
| task-{phase}.2 | {task name} |

## Artifacts Created
| File | Purpose |
|------|---------|
| {path} | {one-line description} |

## Acceptance Criteria Coverage
| AC ID | Description | Status | Implemented In |
|-------|-------------|--------|----------------|
| AC-{x}.{y} | {name} | ✓ | {file or test} |

## Validation Results
- Compilation: PASS | FAIL
- Tests: {n}/{n} passing
- Constraints: {n}/{n} satisfied

## Issues / Decisions
{list any ambiguities resolved, minor deviations, or concerns}

---

**CHECKPOINT REACHED — AWAITING APPROVAL**

Respond with:
- `APPROVED` — proceed to phase {n+1}
- `APPROVED WITH NOTES: {notes}` — proceed with adjustments
- `REVISE: task-{phase}.{n} - {instructions}` — fix before proceeding
- `BLOCKED: {reason}` — stop for discussion
```

---

## Blocker Report Format
```markdown
# BLOCKED: task-{phase}.{n}

## Type
SPEC_AMBIGUITY | SPEC_CONFLICT | TECHNICAL | VALIDATION_FAILURE | DEPENDENCY

## Summary
{one sentence description}

## Details
{full explanation of the problem}

## Evidence
{code snippets, error messages, spec quotes as relevant}

## Attempted Solutions
1. {what you tried} → {result}
2. {what you tried} → {result}

## Options
1. {option}
   - Impact: {on plan, timeline, architecture}
   - Tradeoff: {pros and cons}

2. {option}
   - Impact: {on plan, timeline, architecture}
   - Tradeoff: {pros and cons}

## Recommendation
{which option you suggest and why}

---

**BLOCKED — AWAITING RESOLUTION**

Respond with:
- `PROCEED WITH: {option number}` — continue with specified approach
- `PROCEED WITH: {custom instructions}` — continue with your guidance
- `ABORT TASK` — skip this task, adjust plan
```

---

## Rules

### MUST
- Process tasks in order within each phase
- Verify all prerequisites before starting a task
- Validate all acceptance criteria before marking complete
- Update status.md after each task state change
- Stop at every checkpoint and wait for approval
- Stop immediately when blocked

### MUST NOT
- Modify files in `/specs/` directory
- Skip tasks or reorder tasks within a phase
- Proceed past checkpoint without explicit approval
- Proceed when blocked without resolution
- Create files not specified in the current task card
- Modify files outside current task's scope

### SHOULD
- Reference constraint IDs when applying rules from technical-spec.md
- Record reasoning for non-obvious implementation decisions
- Note potential improvements discovered during implementation

---

## Integration Testing

When running integration tests with testcontainers:
- **MUST** disable the Ryuk container by setting the environment variable:
  ```
  TESTCONTAINERS_RYUK_DISABLED=true
  ```

---

## Quick Reference

| State | Action |
|-------|--------|
| Starting feature | Initialize → Read specs → Read plan → Find current task |
| Starting task | Load card → Check prerequisites → Update status → Execute |
| Task complete | Validate → Update status → Check if phase end |
| Phase complete | Generate checkpoint report → HALT → Wait for approval |
| Blocked | Generate blocker report → HALT → Wait for resolution |
| All phases complete | Generate completion report |

| Task File | Content |
|-----------|---------|
| task-1.1.md | Phase 1, Task 1 |
| task-1.2.md | Phase 1, Task 2 |
| task-2.1.md | Phase 2, Task 1 |
| task-2.2.md | Phase 2, Task 2 |