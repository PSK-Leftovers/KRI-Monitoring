import { useState } from "react";

export default function IndicatorForm({ initial, onSave, onCancel }) {
    const [form, setForm] = useState({
        name: "",
        description: "",
        greenThreshold: "",
        yellowThreshold: "",
        redThreshold: "",
        ...initial,
    });

    const set = (key) => (event) => {
        setForm((current) => ({ ...current, [key]: event.target.value }));
    };

    const handleSubmit = (event) => {
        event.preventDefault();
        onSave({
            ...form,
            greenThreshold: Number(form.greenThreshold),
            yellowThreshold: Number(form.yellowThreshold),
            redThreshold: Number(form.redThreshold),
        });
    };

    const inputClass = "w-full border border-gray-300 rounded-lg px-3 py-2 text-sm text-gray-900 placeholder:text-gray-400 focus:outline-none focus:ring-2 focus:ring-brand-700 focus:border-transparent";

    const thresholds = [
        {
            key: "greenThreshold",
            label: "Žalia riba",
            color: "text-green-700",
            border: "border-green-200",
            bg: "bg-green-50",
            ring: "focus:ring-green-600",
        },
        {
            key: "yellowThreshold",
            label: "Geltona riba",
            color: "text-yellow-700",
            border: "border-yellow-200",
            bg: "bg-yellow-50",
            ring: "focus:ring-yellow-600",
        },
        {
            key: "redThreshold",
            label: "Raudona riba",
            color: "text-red-700",
            border: "border-red-200",
            bg: "bg-red-50",
            ring: "focus:ring-red-600",
        },
    ];

    return (
        <div className="min-h-screen bg-gray-50 px-6 py-8">
            <div className="max-w-2xl mx-auto">
                <button onClick={onCancel} className="flex items-center gap-1 text-sm text-gray-500 hover:text-gray-700 mb-6 cursor-pointer">
                    ← Atgal
                </button>

                <div className="bg-white rounded-xl border border-gray-200 shadow-sm p-8">
                    <h1 className="text-xl font-bold text-gray-900 mb-6">
                        {form.id ? "Redaguoti indikatorių" : "Naujas indikatorius"}
                    </h1>

                    <form onSubmit={handleSubmit} className="space-y-5">
                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">
                                Pavadinimas <span className="text-red-500">*</span>
                            </label>
                            <input
                                required
                                value={form.name}
                                onChange={set("name")}
                                placeholder="pvz. Pajamų rizika"
                                className={inputClass}
                            />
                        </div>

                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">
                                Aprašymas
                            </label>
                            <textarea
                                value={form.description || ""}
                                onChange={set("description")}
                                placeholder="Pvz. Indikatorius yra skirtas pajamų rizikos stebėjimui"
                                rows={3}
                                className={`${inputClass} resize-none`}
                            />
                        </div>

                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-2">
                                Indikatoriaus ribos <span className="text-red-500">*</span>
                            </label>
                            <div className="grid grid-cols-3 gap-3">
                                {thresholds.map(({ key, label, color, border, bg, ring }) => (
                                    <div key={key} className={`border ${border} ${bg} rounded-lg p-3`}>
                                        <label className={`block text-xs font-semibold ${color} mb-1.5`}>
                                            {label}
                                        </label>
                                        <input
                                            required
                                            type="number"
                                            value={form[key]}
                                            onChange={set(key)}
                                            placeholder="pvz. 100"
                                            className={`w-full border border-gray-300 rounded-md px-2.5 py-1.5 text-sm text-gray-900 placeholder:text-gray-400 bg-white focus:outline-none focus:ring-2 ${ring} focus:border-transparent`}
                                        />
                                    </div>
                                ))}
                            </div>
                        </div>

                        <div className="flex justify-end gap-2 pt-2">
                            <button type="button" onClick={onCancel} className="px-4 py-2 text-sm text-gray-700 border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors cursor-pointer">
                                Atšaukti
                            </button>
                            <button type="submit" className="px-4 py-2 text-sm font-medium text-white bg-brand-700 rounded-lg hover:bg-brand-800 transition-colors cursor-pointer">
                                {form.id ? "Išsaugoti pakeitimus" : "Sukurti indikatorių"}
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    );
}