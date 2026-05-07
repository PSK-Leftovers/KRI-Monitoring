export default function DeleteModal({ indicator, onConfirm, onCancel }) {
    return (
        <div className="fixed inset-0 bg-black/40 backdrop-blur-sm flex items-center justify-center z-50">
            <div className="bg-white rounded-xl shadow-xl w-full max-w-sm mx-4">
                <div className="p-6">
                    <h2 className="text-base font-semibold text-gray-900">
                        Ištrinti indikatorių?
                    </h2>
                    <p className="text-sm text-gray-500 mt-1">
                        Ar jūs esate tikras, kad norite ištrinti{" "}<strong className="text-gray-700">"{indicator.name}"</strong>? Šio veiksmo nebus galima sugrąžinti.
                    </p>
                </div>
                <div className="px-6 pb-6 flex justify-end gap-2">
                    <button onClick={onCancel} className="px-4 py-2 text-sm text-gray-700 border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors cursor-pointer">
                        Atšaukti
                    </button>
                    <button onClick={onConfirm} className="px-4 py-2 text-sm font-medium text-white bg-red-600 rounded-lg hover:bg-red-700 transition-colors cursor-pointer">
                        Ištrinti
                    </button>
                </div>
            </div>
        </div>
    );
}