import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import IndicatorForm from "../components/IndicatorForm.jsx";
import DeleteModal from "../components/DeleteModal";
import ValueEntryModal from "../components/ValueEntryModal.jsx";
import IndicatorValuesGraph from "../components/IndicatorValuesGraph.jsx";
import ProtectedButton from "../components/ProtectedButton.jsx";

const API = "http://localhost:8080/api/indicators";

const STATUS_STYLES = {
    GREEN:   "bg-green-100 text-green-700",
    YELLOW:  "bg-yellow-100 text-yellow-700",
    RED:     "bg-red-100 text-red-700",
    UNKNOWN: "bg-gray-100 text-gray-500",
};

const STATUS_LABELS = {
    GREEN:   "Žalia",
    YELLOW:  "Geltona",
    RED:     "Raudona",
    UNKNOWN: "Nežinoma",
};

export default function IndicatorsPage() {
    const [indicators, setIndicators] = useState([]);
    const [editing, setEditing] = useState(null);
    const [deleting, setDeleting] = useState(null);
    const [recordingValue, setRecordingValue] = useState(null);
    const [graphIndicator, setGraphIndicator] = useState(null);
    const [graphData, setGraphData] = useState([]);
    const navigate = useNavigate();
    const currentUser = JSON.parse(localStorage.getItem("user") || "{}");
    const [fromDate, setFromDate] = useState(new Date(new Date().setMonth(new Date().getMonth() - 1)).toISOString().split("T")[0]);
    const [toDate, setToDate] = useState(new Date().toISOString().split("T")[0]);

    const fetchAll = async () => {
        const response = await fetch(API, {credentials: "include"});
        return response.json();
    };

    useEffect(() => {
        fetchAll().then(setIndicators);
    }, []);

    const handleSave = async (data) => {
        const method = data.id ? "PUT" : "POST";
        const url = data.id ? `${API}/${data.id}` : API;

        await fetch(url, {
            method,
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(data),
            credentials: "include"
        });

        const updated = await fetchAll();
        setIndicators(updated);
        setEditing(null);
    };

    const handleDelete = async () => {
        await fetch(`${API}/${deleting.id}`, {
            method: "DELETE",
            credentials: "include"

        });

        const updated = await fetchAll();
        setIndicators(updated);
        setDeleting(null);
    };

    const handleRecordValue = async (value) => {
        await fetch(`${API}/${recordingValue.id}/values`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ value }),
            credentials: "include",
        });

        const updated = await fetchAll();
        setIndicators(updated);
        setRecordingValue(null);
    };

    const openGraph = async (indicator, fromDate, toDate) => {
        setGraphIndicator(indicator);

        const res = await fetch(`${API}/${indicator.id}/values?from=${fromDate}&to=${toDate}`, {
            method: "GET",
            headers: {
                "Content-Type": "application/json"
            },
            credentials: "include"
        });

        const data = await res.json();

        setGraphData(data);
    };

    if (editing !== null) {
        return (
            <IndicatorForm
                initial={editing}
                onSave={handleSave}
                onCancel={() => setEditing(null)}
            />
        );
    }

    return (
        <div className="min-h-screen bg-gray-50 px-6 py-8">
            <div className="max-w-5xl mx-auto">
                <div className="flex items-center justify-between mb-6">
                    <div>
                        <h1 className="text-2xl font-bold text-gray-900">Rizikos indikatoriai</h1>
                        <p className="text-sm text-gray-500 mt-0.5">
                            Sekite ir valdykite savo pagrindinius rizikos indikatorius
                        </p>
                    </div>
                    <div className="flex items-center gap-3">
                        {currentUser.role === "ADMIN" && (
                            <button onClick={() => navigate("/users")} className="text-sm text-gray-500 hover:text-gray-700 cursor-pointer">
                                Vartotojai →
                            </button>
                        )}
                        <button onClick={() => setEditing({})} className="inline-flex items-center gap-2 px-4 py-2 bg-brand-700 text-white text-sm font-medium rounded-lg hover:bg-brand-800 transition-colors cursor-pointer">
                            + Naujas indikatorius
                        </button>
                    </div>
                </div>

                <div className="bg-white rounded-xl border border-gray-200 shadow-sm overflow-hidden">
                    <table className="w-full text-sm">
                        <thead>
                            <tr className="bg-gray-50 border-b border-gray-200">
                                <th className="text-left px-6 py-3 text-xs font-semibold text-gray-500 uppercase tracking-wide">
                                    Pavadinimas
                                </th>
                                <th className="text-left px-6 py-3 text-xs font-semibold text-gray-500 uppercase tracking-wide">
                                    Aprašymas
                                </th>
                                <th className="text-left px-6 py-3 text-xs font-semibold text-gray-500 uppercase tracking-wide">
                                    Dabartinė vertė
                                </th>
                                <th className="text-left px-6 py-3 text-xs font-semibold text-gray-500 uppercase tracking-wide">
                                    Statusas
                                </th>
                                <th className="text-left px-6 py-3 text-xs font-semibold text-gray-500 uppercase tracking-wide">
                                    Ribos
                                </th>
                                <th className="text-left px-6 py-3 text-xs font-semibold text-gray-500 uppercase tracking-wide">
                                    Veiksmai
                                </th>
                            </tr>
                        </thead>
                        <tbody>
                            {indicators.length === 0 ? (
                                <tr>
                                    <td colSpan="6" className="px-6 py-16 text-center text-gray-400 text-sm">
                                        Indikatorių nėra.{" "}
                                        <button onClick={() => setEditing({})} className="text-brand-700 hover:underline">
                                            Sukurkite pirmąjį indikatorių.
                                        </button>
                                    </td>
                                </tr>
                            ) : (
                                indicators.map((indicator) => (
                                    <tr
                                        key={indicator.id}
                                        onClick={() => openGraph(indicator, fromDate, toDate)}
                                        className="border-t border-gray-100 hover:bg-gray-50 transition-colors"
                                    >
                                        <td className="px-6 py-4 font-medium text-gray-900">
                                            {indicator.name}
                                        </td>
                                        <td className="px-6 py-4 text-gray-500 max-w-xs truncate">
                                            {indicator.description || (
                                                <span className="text-gray-300 italic">—</span>
                                            )}
                                        </td>
                                        <td className="px-6 py-4">
                                            {indicator.latestValue != null ? (
                                                <span className={`inline-flex items-center px-2 py-0.5 rounded-full text-xs font-medium ${STATUS_STYLES[indicator.status] ?? STATUS_STYLES.UNKNOWN}`}>
                                                    {indicator.latestValue}
                                                </span>
                                            ) : (
                                                <span className="text-xs text-gray-400 italic">Nėra duomenų</span>
                                            )}
                                        </td>
                                        <td className="px-6 py-4">
                                            <span className={`inline-flex items-center px-2 py-0.5 rounded-full text-xs font-medium ${STATUS_STYLES[indicator.status] ?? STATUS_STYLES.UNKNOWN}`}>
                                                {STATUS_LABELS[indicator.status] ?? indicator.status}
                                            </span>
                                        </td>
                                        <td className="px-6 py-4">
                                            <div className="flex items-center gap-1.5">
                                                <span className="inline-flex items-center px-2 py-0.5 rounded-full text-xs font-medium bg-green-100 text-green-700">
                                                    {indicator.greenThreshold}
                                                </span>
                                                <span className="inline-flex items-center px-2 py-0.5 rounded-full text-xs font-medium bg-yellow-100 text-yellow-700">
                                                    {indicator.yellowThreshold}
                                                </span>
                                                <span className="inline-flex items-center px-2 py-0.5 rounded-full text-xs font-medium bg-red-100 text-red-700">
                                                    {indicator.redThreshold}
                                                </span>
                                            </div>
                                        </td>
                                        <td className="px-6 py-4">
                                            <div className="flex items-center gap-3">
                                                <button onClick={(e) => { e.stopPropagation(); setRecordingValue(indicator); }} className="text-xs text-brand-700 hover:text-brand-800 font-medium hover:underline cursor-pointer">
                                                    Įvesti
                                                </button>
                                                <button onClick={(e) => { e.stopPropagation(); setEditing(indicator); }} className="text-xs text-brand-700 hover:text-brand-800 font-medium hover:underline cursor-pointer">
                                                    Redaguoti
                                                </button>
                                                <ProtectedButton onClick={(e) => { e.stopPropagation(); setDeleting(indicator); }} className="text-xs text-red-600 hover:text-red-700 font-medium hover:underline cursor-pointer">
                                                    Ištrinti
                                                </ProtectedButton>
                                            </div>
                                        </td>
                                    </tr>
                                ))
                            )}
                        </tbody>
                    </table>
                </div>
            </div>

            {deleting && (
                <DeleteModal
                    indicator={deleting}
                    onConfirm={handleDelete}
                    onCancel={() => setDeleting(null)}
                />
            )}

            {recordingValue && (
                <ValueEntryModal
                    indicator={recordingValue}
                    onSave={handleRecordValue}
                    onCancel={() => setRecordingValue(null)}
                />
            )}

            {graphIndicator && (
                <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
                    <div className="bg-white rounded-xl shadow-lg w-[800px] h-[500px] p-6 relative">

                        <button
                            onClick={() => { setGraphIndicator(null); setFromDate(new Date(new Date().setMonth(new Date().getMonth() - 1)).toISOString().split("T")[0]); setToDate(new Date().toISOString().split("T")[0]); setGraphData([]);}}
                            className="absolute top-3 right-3 text-gray-500 hover:text-black"
                        >
                            ✕
                        </button>

                        <h2 className="text-lg font-semibold mb-4">
                            {graphIndicator.name} istorija
                        </h2>

                        {graphData.length > 0 ? (
                            <IndicatorValuesGraph graphData={graphData} greenThreshold={graphIndicator.greenThreshold} yellowThreshold={graphIndicator.yellowThreshold} redThreshold={graphIndicator.redThreshold} />
                            ) : (
                                <div className="flex items-center justify-center h-[350px] text-gray-500">
                                    Nėra duomenų
                                </div>
                            )
                        }

                        <div className="flex gap-4">

                            <h2 className="text-lg text-gray-600 mt-3">
                                Filtravimas:
                            </h2>

                            <div className="flex">
                                <label className="text-sm text-gray-700 mt-3 pl-12 pr-4">
                                    Nuo:
                                </label>

                                <input
                                    type="date"
                                    value={fromDate}
                                    onChange={(e) => setFromDate(e.target.value)}
                                    className="border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
                                />
                            </div>

                            <div className="flex">
                                <label className="text-sm text-gray-700 mt-3 pl-6 pr-4">
                                    Iki:
                                </label>

                                <input
                                    type="date"
                                    value={toDate}
                                    onChange={(e) => setToDate(e.target.value)}
                                    className="border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
                                />
                            </div>
                            
                            <button
                                onClick={() => openGraph(graphIndicator, fromDate, toDate)}
                                className="bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700 ml-4"
                            >
                                Filtruoti
                            </button>
                        </div>

                    </div>
                </div>
            )}
        </div>
    );
}
