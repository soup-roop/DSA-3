# Advanced Gamified Project Scheduling & Dynamic Resource Allocation System

A high-performance project scheduling engine and dual-interface management suite built in Java. The system models complex workflows as Directed Acyclic Graphs (DAGs), resolves dependencies, pinpoints critical operational bottlenecks, allocates resources using network flow optimization, and delivers two distinct operational views: **Overwatch** (Enterprise Analytics) and **Overdrive** (Gamified RPG Quest Dashboard).

---

## Table of Contents

* [Overview](https://www.google.com/search?q=%23overview)
* [Key Features](https://www.google.com/search?q=%23key-features)
* [Data Structures & Algorithms (Course Outcomes)](https://www.google.com/search?q=%23data-structures--algorithms-course-outcomes)
* [System Architecture](https://www.google.com/search?q=%23system-architecture)
* [CSV Data Schema](https://www.google.com/search?q=%23csv-data-schema)
* [Getting Started](https://www.google.com/search?q=%23getting-started)
* [Team & Academic Credits](https://www.google.com/search?q=%23team--academic-credits)

---

## Overview

Modern project management tools often suffer from computational rigidity and user fatigue. This platform addresses both by:

1. Guaranteeing mathematical correctness in task sequencing and deadlock interception.
2. Formulating workforce assignment as a capacitated network flow problem.
3. Offering two interchangeable UI paradigms:
* **Overwatch UI:** An enterprise terminal featuring critical-path indicators, slack metrics, and deadline monitors.
* **Overdrive UI:** An RPG campaign dashboard transforming tasks into quests complete with rarity tiers (Common to Legendary), dynamic XP scaling, and leveling progression.



---

## Key Features

* **Deadlock & Cycle Detection:** Proactively detects cyclic task dependencies to prevent scheduling deadlocks during data ingestion.
* **Critical Path Identification:** Calculates Earliest/Latest schedule bounds and isolates zero-slack activities that dictate overall project duration.
* **Capacitated Workforce Allocation:** Dynamically matches pending tasks to team members based on defined capacity quotas, cleanly isolating unassigned tasks.
* **Linear-Time Task Search:** Backtrack-free search engine that filters active tasks and quests by ID or title.
* **Dual Visual Paradigms:** Custom Java 2D dark-mode rendering across both the enterprise analytics table and the gamified quest view.
* **Robust CSV Ingestion & Sync:** Resilient parser supporting deadline calculations relative to import time and persistent local storage.

---

## Data Structures & Algorithms (Course Outcomes)

| Course Outcome | Applied Algorithm | Underlying Data Structures | Complexity | System Role |
| --- | --- | --- | --- | --- |
| **CO1** | **Kahn’s Algorithm** (Topological Sort via BFS) | `Map<String, List<String>>` (Adjacency List), `Map<String, Integer>` (In-Degree Map), `Queue<String>` | $\mathcal{O}(V + E)$ | Task dependency resolution and cycle/deadlock interception |
| **CO2** | **Knuth-Morris-Pratt (KMP)** Pattern Matching | 1D Integer Array ($\pi$ / LPS Table) | $\mathcal{O}(N + M)$ | Real-time linear-time task and quest search without backtracking |
| **CO3** | **Critical Path Method (CPM)** via Two-Pass DP | Memoized State Tables (`EST`, `EFT`, `LST`, `LFT`, `Slack`) | $\mathcal{O}(V + E)$ | Forward/backward pass dynamic programming to compute slack and critical tasks |
| **CO4** | **Edmonds-Karp Algorithm** (Max Flow / Min Cut) | 2D Capacity Matrix, Residual Matrix, BFS Queue, Parent Array | $\mathcal{O}(V \cdot E^2)$ | Capacitated bipartite matching for workforce allocation and overflow isolation |

---

## System Architecture

```text
├── src/
│   ├── dsa/
│   │   ├── GraphEngine.java     # Core DAG engine, Kahn's, CPM DP, Edmonds-Karp, KMP
│   │   ├── Task.java            # Task/Quest model with temporal and gamified metadata
│   │   ├── User.java            # User credentials, level, and XP progression
│   │   └── UserManager.java     # Local user profile persistence
│   └── ui/
│       ├── AuthWindow.java      # Login and account registration
│       ├── ModeWindow.java      # Interface selection (Overwatch vs. Overdrive)
│       ├── OverwatchUI.java     # Enterprise analytics dashboard
│       └── OverdriveUI.java     # Gamified RPG campaign dashboard
└── data/
    └── sample_project.csv       # Standard project dependency dataset

```

---

## CSV Data Schema

Project schedules are loaded from standard comma-delimited files following this format:

```csv
TaskID,TaskName,DurationHours,TimeLimitHours,Dependencies,IsCompleted,TimeMode,DeadlineMs

```

* **TaskID:** Unique identifier (e.g., `T1`, `T2`).
* **TaskName:** Descriptive title of the task.
* **DurationHours:** Integer duration in hours.
* **TimeLimitHours:** Optional constraint (leave empty if none).
* **Dependencies:** Semicolon-delimited list of prerequisite IDs (or `none`).
* **IsCompleted:** `true` or `false`.
* **TimeMode:** `TIMER` or `NONE`.
* **DeadlineMs:** Epoch millisecond timestamp. Set to `0` to automatically compute deadlines relative to present import time (`System.currentTimeMillis() + Duration * 3600000L`).

### Sample Dataset

```csv
T1,Define System Architecture,24,,none,false,TIMER,0
T2,Database Schema Migration,48,,T1,false,TIMER,0
T3,Core API Development,72,,T1,false,TIMER,0
T4,Frontend Integration,48,,T2;T3,false,TIMER,0
T5,Security Penetration Testing,36,,T4,false,TIMER,0

```

---

## Getting Started

### Prerequisites

* **Java Development Kit (JDK):** Version 17 or higher
* **Git**

### Installation & Execution

1. **Clone the repository:**
```bash
git clone https://github.com/your-username/project-scheduling-dsa.git
cd project-scheduling-dsa

```


2. **Compile the source files:**
```bash
javac -d bin src/dsa/*.java src/ui/*.java

```


3. **Launch the application:**
```bash
java -cp bin ui.AuthWindow

```



---

## Team & Academic Credits

This project was developed as part of the academic curriculum for the Department of Computer Science and Engineering at **K L University**.

* **Team Members:**
* Aakash Dadhirao (2520030337)
* Souradeepta Das (2520030347)
* V. Sai Nikhil (2520030255)


* **Faculty Guide:** Dr. S. Madhavi, Assistant Professor, Department of Computer Science and Engineering
