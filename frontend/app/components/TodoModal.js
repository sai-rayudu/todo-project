"use client";
import { useState, useEffect } from "react";

const PRIORITIES = ["LOW", "MEDIUM", "HIGH"];

const defaultForm = {
    title: "",
    description: "",
    priority: "MEDIUM",
    dueDate: "",
    completed: false,
};

export default function TodoModal({ isOpen, onClose, onSave, initialData, loading }) {
    const [form, setForm] = useState(defaultForm);

    useEffect(() => {
        if (isOpen) {
            if (initialData) {
                setForm({
                    title: initialData.title || "",
                    description: initialData.description || "",
                    priority: initialData.priority || "MEDIUM",
                    // Strip time portion from ISO datetime strings so <input type="date"> works correctly
                    dueDate: initialData.dueDate ? initialData.dueDate.split("T")[0] : "",
                    completed: initialData.completed ?? false,
                });
            } else {
                setForm(defaultForm);
            }
        }
    }, [isOpen, initialData]);

    if (!isOpen) return null;

    const handleChange = (e) => {
        const { name, value, type, checked } = e.target;
        setForm((prev) => ({ ...prev, [name]: type === "checkbox" ? checked : value }));
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        const payload = {
            ...form,
            dueDate: form.dueDate || null,
        };
        onSave(payload);
    };

    const labelStyle = {
        display: "block",
        fontSize: 13,
        fontWeight: 500,
        color: "#c0c0e0",
        marginBottom: 6,
    };

    return (
        <div className="modal-overlay" onClick={(e) => e.target === e.currentTarget && onClose()}>
            <div
                className="glass animate-fadeInUp"
                style={{ width: "100%", maxWidth: 480, padding: "32px 28px" }}
            >
                <div style={{ display: "flex", alignItems: "center", justifyContent: "space-between", marginBottom: 24 }}>
                    <h2 style={{ fontSize: 20, fontWeight: 700, color: "#f1f1f1" }}>
                        {initialData ? "Edit Todo" : "Create Todo"}
                    </h2>
                    <button
                        onClick={onClose}
                        style={{
                            background: "transparent",
                            border: "none",
                            color: "#a0a0c0",
                            fontSize: 20,
                            cursor: "pointer",
                            padding: 4,
                            lineHeight: 1,
                            borderRadius: 6,
                            transition: "color 0.2s",
                        }}
                        onMouseEnter={(e) => (e.currentTarget.style.color = "#f1f1f1")}
                        onMouseLeave={(e) => (e.currentTarget.style.color = "#a0a0c0")}
                    >
                        ✕
                    </button>
                </div>

                <form onSubmit={handleSubmit} style={{ display: "flex", flexDirection: "column", gap: 16 }}>
                    <div>
                        <label style={labelStyle}>Title *</label>
                        <input
                            className="input-field"
                            type="text"
                            name="title"
                            placeholder="What needs to be done?"
                            value={form.title}
                            onChange={handleChange}
                            required
                        />
                    </div>

                    <div>
                        <label style={labelStyle}>Description</label>
                        <textarea
                            className="input-field"
                            name="description"
                            placeholder="Add more details (optional)…"
                            value={form.description}
                            onChange={handleChange}
                            rows={3}
                            style={{ resize: "vertical", minHeight: 72 }}
                        />
                    </div>

                    <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 12 }}>
                        <div>
                            <label style={labelStyle}>Priority</label>
                            <select
                                className="input-field"
                                name="priority"
                                value={form.priority}
                                onChange={handleChange}
                            >
                                {PRIORITIES.map((p) => (
                                    <option key={p} value={p}>
                                        {p}
                                    </option>
                                ))}
                            </select>
                        </div>

                        <div>
                            <label style={labelStyle}>Due Date</label>
                            <input
                                className="input-field"
                                type="date"
                                name="dueDate"
                                value={form.dueDate}
                                onChange={handleChange}
                                style={{ colorScheme: "dark" }}
                            />
                        </div>
                    </div>

                    {initialData && (
                        <label
                            style={{
                                display: "flex",
                                alignItems: "center",
                                gap: 10,
                                cursor: "pointer",
                                padding: "10px 14px",
                                background: "rgba(255,255,255,0.04)",
                                border: "1px solid rgba(255,255,255,0.1)",
                                borderRadius: 10,
                            }}
                        >
                            <input
                                type="checkbox"
                                name="completed"
                                checked={form.completed}
                                onChange={handleChange}
                                style={{ width: 16, height: 16, accentColor: "var(--accent)" }}
                            />
                            <span style={{ fontSize: 14, color: "#c0c0e0" }}>Mark as completed</span>
                        </label>
                    )}

                    <div style={{ display: "flex", gap: 10, marginTop: 4 }}>
                        <button
                            type="button"
                            className="btn-ghost"
                            onClick={onClose}
                            style={{ flex: 1 }}
                        >
                            Cancel
                        </button>
                        <button
                            type="submit"
                            className="btn-primary"
                            disabled={loading}
                            style={{ flex: 2 }}
                        >
                            {loading ? "Saving…" : initialData ? "Save Changes" : "Create Todo"}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
}
