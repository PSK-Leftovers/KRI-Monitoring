import {
    LineChart,
    Line,
    XAxis,
    YAxis,
    Tooltip,
    ResponsiveContainer,
    CartesianGrid
} from "recharts";

export default function IndicatorValuesGraph({ graphData }) {
    return (
        <div className="w-full h-[350px]">
            <ResponsiveContainer width="100%" height="100%">
                <LineChart data={graphData}>
                    <CartesianGrid strokeDasharray="3 3" />

                    <XAxis
                        dataKey="recordedAt"
                        tickFormatter={(v) =>
                            v.slice(0, 10)
                        }
                    />

                    <YAxis />

                    <Tooltip
                        labelFormatter={(v) =>
                            v.slice(0, 19).replace("T", " ")
                        }
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