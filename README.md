# qbit-standard-process-trace

Process execution tracing and logging for QQQ applications.

**For:** QQQ developers who need visibility into ETL process execution  
**Status:** Stable

## Why This Exists

ETL processes run in the background. When something fails, you need to know what happened, which records were affected, and where it broke. Adding logging to every process is repetitive and inconsistent.

This QBit provides automatic tracing for QQQ processes. Every execution is logged with timing, record counts, and error details. No code changes required in your processes.

## Features

- **Automatic Tracing** - Captures all process executions without code changes
- **Step-Level Detail** - Timing and status for each step in multi-step processes
- **Record Tracking** - Input/output counts and error record IDs
- **Error Capture** - Full stack traces and error messages
- **Dashboard View** - Process history visible in the QQQ dashboard
- **Retention Policies** - Automatic cleanup of old trace data

## Quick Start

### Prerequisites

- QQQ application (v0.20+)
- Database backend configured

### Installation

Add to your `pom.xml`:

```xml
<dependency>
    <groupId>io.qrun</groupId>
    <artifactId>qbit-standard-process-trace</artifactId>
    <version>0.1.0</version>
</dependency>
```

### Register the QBit

```java
public class AppMetaProvider extends QMetaProvider {
    @Override
    public void configure(QInstance qInstance) {
        new StandardProcessTraceQBit().configure(qInstance);
    }
}
```

That's it. All processes are now traced automatically.

## Usage

### Viewing Traces

Traces appear in the dashboard under the Process Trace screen. Each trace shows:

- Process name and start time
- Duration
- Status (success, error, warning)
- Record counts (input, output, error)
- Step-by-step breakdown

### Querying Traces Programmatically

```java
QueryInput query = new QueryInput()
    .withTableName("process_trace")
    .withFilter(new QQueryFilter()
        .withCriteria("processName", Operator.EQUALS, "orderSync")
        .withCriteria("status", Operator.EQUALS, "ERROR"))
    .withOrderBy(new QFilterOrderBy("startTime", false));
```

### Custom Trace Data

Add custom data to the current trace:

```java
ProcessTraceContext.current()
    .withCustomField("batchId", batchId)
    .withCustomField("sourceSystem", "ERP");
```

### Selective Tracing

Disable tracing for specific processes:

```java
new QProcessMetaData()
    .withName("frequentHealthCheck")
    .withTracingEnabled(false);  // Don't log every execution
```

## Configuration

The QBit creates these tables:

| Table | Purpose |
|-------|---------|
| `process_trace` | Execution summary records |
| `process_trace_step` | Step-level detail |
| `process_trace_error` | Error records with details |

### Retention

```java
new StandardProcessTraceQBit()
    .withRetentionDays(30)  // Keep traces for 30 days
    .withCleanupSchedule("0 0 2 * * *");  // Run cleanup at 2 AM
```

## Project Status

Stable and production-ready.

### Roadmap

- Trace aggregation and statistics
- Alerting on repeated failures
- Export to external monitoring systems

## Contributing

1. Fork the repository
2. Create a feature branch
3. Run tests: `mvn clean verify`
4. Submit a pull request

## License

Proprietary - QRun.IO
