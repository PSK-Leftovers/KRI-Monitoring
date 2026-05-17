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

export default function IndicatorValuesGraph({ graphData, greenThreshold, yellowThreshold, redThreshold }) {
    const allValues = graphData.map(d => Number(d.value));

    const minY = Math.min(
        ...allValues,
        greenThreshold,
        yellowThreshold,
        redThreshold
    );

    const maxY = Math.max(
        ...allValues,
        greenThreshold,
        yellowThreshold,
        redThreshold
    );

    return (
        <div className="w-full h-[350px]">
            <ResponsiveContainer width="100%" height="95%">
                <LineChart data={graphData}>
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

                    <ReferenceLine
                        y={redThreshold}
                        stroke="red"
                        strokeDasharray="3 3"
                    />

                    <ReferenceLine
                        y={yellowThreshold}
                        stroke="yellow"
                        strokeDasharray="3 3"
                    />

                    <ReferenceLine
                        y={greenThreshold}
                        stroke="green"
                        strokeDasharray="3 3"
                    />

                    <Line
                        type="monotone"
                        dataKey="value"
                        stroke="#2563eb"
                        strokeWidth={2}
                        dot={false}
                    />
                </LineChart>
            </ResponsiveContainer>
        </div>
    );
}