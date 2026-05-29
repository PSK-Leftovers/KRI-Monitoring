export default function VersionConflictModal({ onRefresh, onOverwrite, onCancel }) {
    return (
        <div className="fixed inset-0 bg-black/60 flex items-center justify-center z-50">
            <div className="bg-white rounded-xl shadow-xl p-8 w-full max-w-md">
                <h2 className="text-lg font-bold text-gray-900 mb-3">Duomenų konfliktas</h2>
                <p className="text-sm text-gray-600 mb-6">
                    Kitas naudotojas pakeitė šiuos duomenis tuo metu, kai jūs redagavote.
                    Ką norėtumėte daryti?
                </p>
                <div className="flex flex-col gap-3">
                    <button
                        onClick={onRefresh}
                        className="w-full py-2.5 bg-brand-700 text-white text-sm font-medium rounded-lg hover:bg-brand-800 transition-colors cursor-pointer"
                    >
                        Atnaujinti ir pakartoti redagavimą
                    </button>
                    <button
                        onClick={onOverwrite}
                        className="w-full py-2.5 border border-gray-300 text-gray-700 text-sm font-medium rounded-lg hover:bg-gray-50 transition-colors cursor-pointer"
                    >
                        Rašyti ant viršaus
                    </button>
                    <button
                        onClick={onCancel}
                        className="w-full py-2.5 text-gray-400 text-sm hover:text-gray-600 transition-colors cursor-pointer"
                    >
                        Atšaukti
                    </button>
                </div>
            </div>
        </div>
    );
}
