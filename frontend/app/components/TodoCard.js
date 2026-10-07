"use client";

export default function TodoCard({ todo, onEdit, onDelete, onToggle }) {
    const priorityColors = {
        HIGH: { bg: "rgba(239,68,68,0.12)", text: "#f87171", border: "rgba(239,68,68,0.3)" },
        MEDIUM: { bg: "rgba(245,158,11,0.12)", text: "#fbbf24", border: "rgba(245,158,11,0.3)" },
        LOW: { bg: "rgba(34,197,94,0.12)", text: "#4ade80", border: "rgba(34,197,94,0.3)" },
    };

    const colors = priorityColors[todo.priority] || priorityColors.LOW;

    const formatDate = (dateStr) => {
        if (!dateStr) return null;
        try {
            return new Date(dateStr).toLocaleDateString("en-US", {
                month: "short",
                day: "numeric",
                year: "numeric",
            });
        } catch {
            return dateStr;
        }
    };

    const isOverdue =
        todo.dueDate && !todo.completed && new Date(todo.dueDate) < new Date();

    return (
        <div
            className="glass animate-fadeInUp"
            style={{
                padding: "18px 20px",
                borderRadius: 14,
                opacity: todo.completed ? 0.72 : 1,
                transition: "opacity 0.2s, transform 0.2s, box-shadow 0.2s",
                cursor: "default",
                position: "relative",
                overflow: "hidden",
            }}
            onMouseEnter={(e) => {
                e.currentTarget.style.transform = "translateY(-2px)";
                e.currentTarget.style.boxShadow = "0 8px 30px rgba(0,0,0,0.3)";
            }}
            onMouseLeave={(e) => {
                e.currentTarget.style.transform = "translateY(0)";
                e.currentTarget.style.boxShadow = "";
            }}
        >
            {/* Left accent bar */}
            <div
                style={{
                    position: "absolute",
                    left: 0,
                    top: 0,
                    bottom: 0,
                    width: 4,
                    background: colors.text,
                    borderRadius: "14px 0 0 14px",
                    opacity: 0.7,
                }}
            />

            <div style={{ display: "flex", alignItems: "flex-start", gap: 14, paddingLeft: 8 }}>
                {/* Content */}
                <div style={{ flex: 1, minWidth: 0 }}>
                    <div style={{ display: "flex", alignItems: "center", gap: 10, flexWrap: "wrap", marginBottom: 4 }}>
                        <h3
                            style={{
                                fontSize: 15,
                                fontWeight: 600,
                                color: todo.completed ? "#a0a0c0" : "#f1f1f1",
                                textDecoration: todo.completed ? "line-through" : "none",
                                overflow: "hidden",
                                textOverflow: "ellipsis",
                                whiteSpace: "nowrap",
                                maxWidth: 280,
                            }}
                        >
                            {todo.title}
                        </h3>

                        {/* Priority badge */}
                        {todo.priority && (
                            <span
                                style={{
                                    padding: "2px 10px",
                                    borderRadius: 20,
                                    fontSize: 11,
                                    fontWeight: 600,
                                    letterSpacing: "0.5px",
                                    background: colors.bg,
                                    color: colors.text,
                                    border: `1px solid ${colors.border}`,
                                    whiteSpace: "nowrap",
                                }}
                            >
                                {todo.priority}
                            </span>
                        )}
                    </div>

                    {todo.description && (
                        <p
                            style={{
                                fontSize: 13,
                                color: "#8888aa",
                                marginBottom: 8,
                                overflow: "hidden",
                                display: "-webkit-box",
                                WebkitLineClamp: 2,
                                WebkitBoxOrient: "vertical",
                                lineHeight: 1.5,
                            }}
                        >
                            {todo.description}
                        </p>
                    )}

                    {/* Meta: due date */}
                    {todo.dueDate && (
                        <span
                            style={{
                                fontSize: 12,
                                color: isOverdue ? "#f87171" : "#6060a0",
                                display: "inline-flex",
                                alignItems: "center",
                                gap: 4,
                            }}
                        >
                            {isOverdue ? "⚠ " : "📅 "}
                            {formatDate(todo.dueDate)}
                            {isOverdue && !todo.completed && " (overdue)"}
                        </span>
                    )}
                </div>

                {/* Actions */}
                <div style={{ display: "flex", gap: 6, flexShrink: 0 }}>
                    <button
                        onClick={() => onToggle(todo)}
                        title={todo.completed ? "Mark Incomplete" : "Mark Complete"}
                        style={{
                            padding: "6px 12px",
                            borderRadius: 8,
                            border: todo.completed ? "1px solid rgba(16,185,129,0.25)" : "none",
                            background: todo.completed ? "rgba(16,185,129,0.1)" : "var(--accent)",
                            color: todo.completed ? "var(--accent-light)" : "white",
                            cursor: "pointer",
                            fontSize: 13,
                            display: "flex",
                            alignItems: "center",
                            justifyContent: "center",
                            transition: "all 0.2s",
                            fontWeight: 600,
                            boxShadow: todo.completed ? "none" : "0 2px 10px var(--accent-glow)",
                        }}
                        onMouseEnter={(e) => {
                            if (!todo.completed) {
                                e.currentTarget.style.transform = "scale(1.05)";
                                e.currentTarget.style.boxShadow = "0 4px 15px var(--accent-glow)";
                            } else {
                                e.currentTarget.style.background = "rgba(16,185,129,0.22)";
                            }
                        }}
                        onMouseLeave={(e) => {
                            if (!todo.completed) {
                                e.currentTarget.style.transform = "scale(1)";
                                e.currentTarget.style.boxShadow = "0 2px 10px var(--accent-glow)";
                            } else {
                                e.currentTarget.style.background = "rgba(16,185,129,0.1)";
                            }
                        }}
                    >
                        {todo.completed ? "Revert" : "Complete"}
                    </button>
                    <button
                        onClick={() => onEdit(todo)}
                        title="Edit"
                        style={{
                            width: 32,
                            height: 32,
                            borderRadius: 8,
                            border: "1px solid rgba(16,185,129,0.25)",
                            background: "rgba(16,185,129,0.1)",
                            color: "var(--accent-light)",
                            cursor: "pointer",
                            fontSize: 14,
                            display: "flex",
                            alignItems: "center",
                            justifyContent: "center",
                            transition: "background 0.2s, transform 0.1s",
                        }}
                        onMouseEnter={(e) => {
                            e.currentTarget.style.background = "rgba(16,185,129,0.22)";
                            e.currentTarget.style.transform = "scale(1.1)";
                        }}
                        onMouseLeave={(e) => {
                            e.currentTarget.style.background = "rgba(16,185,129,0.1)";
                            e.currentTarget.style.transform = "scale(1)";
                        }}
                    >
                        ✏️
                    </button>
                    <button
                        onClick={() => onDelete(todo)}
                        title="Delete"
                        style={{
                            width: 32,
                            height: 32,
                            borderRadius: 8,
                            border: "1px solid rgba(239,68,68,0.25)",
                            background: "rgba(239,68,68,0.08)",
                            color: "#f87171",
                            cursor: "pointer",
                            fontSize: 14,
                            display: "flex",
                            alignItems: "center",
                            justifyContent: "center",
                            transition: "background 0.2s, transform 0.1s",
                        }}
                        onMouseEnter={(e) => {
                            e.currentTarget.style.background = "rgba(239,68,68,0.2)";
                            e.currentTarget.style.transform = "scale(1.1)";
                        }}
                        onMouseLeave={(e) => {
                            e.currentTarget.style.background = "rgba(239,68,68,0.08)";
                            e.currentTarget.style.transform = "scale(1)";
                        }}
                    >
                        🗑️
                    </button>
                </div>
            </div>
        </div>
    );
}
