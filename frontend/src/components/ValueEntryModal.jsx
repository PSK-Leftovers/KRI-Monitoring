import { useState } from "react";

export default function ValueEntryModal({ indicator, onSave, onCancel }) {
    const [value, setValue] = useState("");
    const [datetime, setDatetime] = useState()
    const [saving, setSaving] = useState(false);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setSaving(true);
        await onSave(parseFloat(value));
        setSaving(false);
    };

    return (
        <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50">
            <div className="bg-white rounded-xl shadow-lg w-full max-w-sm p-6">
                <h2 className="text-lg font-semibold text-gray-900 mb-1">Įvesti reikšmę</h2>
                <p className="text-sm text-gray-500 mb-4">{indicator.name}</p>
                <form onSubmit={handleSubmit} className="space-y-4">
                    <input
                        type="number"
                        step="any"
                        required
                        autoFocus
                        placeholder="Reikšmė"
                        value={value}
                        onChange={(e) => setValue(e.target.value)}
                        className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-brand-700"
                    />
                    <input
                        type="datetime-local"
                        value={datetime}
                        onChange={(e) => setDatetime(e.target.value)}
                        className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-brand-700"
                    />
                    <div className="flex gap-2 justify-end">
                        <button type="button" onClick={onCancel} className="px-4 py-2 text-sm text-gray-700 border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors cursor-pointer">
                            Atšaukti
                        </button>
                        <button type="submit" disabled={saving} className="px-4 py-2 text-sm font-medium text-white bg-brand-700 rounded-lg hover:bg-brand-800 transition-colors disabled:opacity-50 cursor-pointer">
                            {saving ? "Saugoma..." : "Išsaugoti"}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
}
