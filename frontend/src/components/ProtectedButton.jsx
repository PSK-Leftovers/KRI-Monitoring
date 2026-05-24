import { usePermissions } from "../hooks/usePermissions";

export default function ProtectedButton({
                                            children,
                                            onClick,
                                            className = "",
                                            disabledClass = "opacity-40 cursor-not-allowed", // Standartinė išjungto mygtuko išvaizda
                                            ...props
                                        }) {
    const { canModify } = usePermissions();

    if (!canModify) {
        return (
            <button
                type="button"
                disabled
                title="Neturite teisių atlikti šį veiksmą"
                className={`${className} ${disabledClass}`}
                {...props}
            >
                {children}
            </button>
        );
    }

    return (
        <button type="button" onClick={onClick} className={className} {...props}>
            {children}
        </button>
    );
}