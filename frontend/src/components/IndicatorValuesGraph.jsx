import {
    LineChart,
    Line,
    XAxis,
    YAxis,
    Tooltip,
    ResponsiveContainer,
    CartesianGrid,
    ReferenceLine
} from "recharts";

function stepAfter(arr, time) {
    const sorted = [...arr].sort((a, b) => a.recordedAt.localeCompare(b.recordedAt));
    let last = null;
    for (const entry of sorted) {
        if (entry.recordedAt <= time) last = entry.value;
        else break;
    }
    return last;
}

export default function IndicatorValuesGraph({ graphData, greenThreshold, yellowThreshold, redThreshold }) {
    const allValues = graphData.map(d => Number(d.value));

    const thresholdValues = [
    ...greenThreshold,
    ...yellowThreshold,
    ...redThreshold
    ].map(d => Number(d.value));

    const mergedData = graphData.map(d => ({
            recordedAt: d.recordedAt,
            value:  d.value,
            green:  stepAfter(greenThreshold,  d.recordedAt),
            yellow: stepAfter(yellowThreshold, d.recordedAt),
            red:    stepAfter(redThreshold,    d.recordedAt),
        }));

    const allNumbers = mergedData.flatMap(d =>
        [d.value, d.green, d.yellow, d.red].filter(v => v != null)
    );

    const minY = Math.min(...allNumbers);
    const maxY = Math.max(...allNumbers);

    return (
        <div className="w-full h-[350px]">
            <ResponsiveContainer width="100%" height="95%">
                <LineChart data={mergedData}>
                    <CartesianGrid strokeDasharray="3 3" />

                    <XAxis
                        dataKey="recordedAt"
                        tickFormatter={(v) =>
                            v.slice(0, 10)
                        }
                    />

                    <YAxis 
                        domain={[minY, maxY]}
                    />

                    <Tooltip
                        labelFormatter={(v) =>
                            v.slice(0, 19).replace("T", " ")
                        }
                    />

                    <Line
                        dataKey="value"
                        stroke="#2563eb"
                        dot={false}
                    />
                    
                    <Line
                        dataKey="green"
                        type="stepAfter"
                        stroke="green"
                        dot={false}
                    />

                    <Line
                        dataKey="yellow"
                        type="stepAfter"
                        stroke="yellow"
                        dot={false}
                    />

                    <Line
                        dataKey="red"
                        type="stepAfter"
                        stroke="red"
                        dot={false}
                    />

                </LineChart>
            </ResponsiveContainer>
        </div>
    );
}