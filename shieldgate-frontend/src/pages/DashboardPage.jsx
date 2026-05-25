import { useEffect, useState } from "react";
import Navbar from "../components/Navbar";
import api, { getSecurityStats, getSecurityEvents } from "../api/client";

function DashboardPage() {
  const [keys, setKeys] = useState([]);
  const [securityStats, setSecurityStats] = useState({
    totalEvents: 0,
    criticalEvents: 0,
    highEvents: 0,
    mediumEvents: 0,
  });

  const [securityEvents, setSecurityEvents] = useState([]);
  const [loading, setLoading] = useState(true);

  const [showCreateModal, setShowCreateModal] = useState(false);
  const [showActionModal, setShowActionModal] = useState(false);

  const [actionType, setActionType] = useState(null);
  const [selectedKey, setSelectedKey] = useState(null);

  const [searchTerm, setSearchTerm] = useState("");
  const [filterStatus, setFilterStatus] = useState("ALL");

  const [newKey, setNewKey] = useState({
    email: "yash@test.com",
    name: "",
    dailyQuota: 1000,
  });

  const fetchKeys = async () => {
    try {
      const res = await api.get("/api/keys/my?email=yash@test.com");
      setKeys(res.data);
    } catch (err) {
      console.error("Failed to fetch keys:", err);
    }
  };

  const fetchSecurityData = async () => {
    try {
      const [statsRes, eventsRes] = await Promise.all([
        getSecurityStats(),
        getSecurityEvents(),
      ]);

      setSecurityStats(statsRes.data);
      setSecurityEvents(eventsRes.data);
    } catch (err) {
      console.error("Failed to fetch security analytics:", err);
    }
  };

  const loadDashboard = async () => {
    setLoading(true);
    await Promise.all([fetchKeys(), fetchSecurityData()]);
    setLoading(false);
  };

  useEffect(() => {
    loadDashboard();
  }, []);

  const activeKeys = keys.filter((k) => k.active).length;
  const disabledKeys = keys.filter((k) => !k.active).length;
  const totalRequests = keys.reduce((sum, k) => sum + k.requestsUsed, 0);

  const maskKey = (key) => {
    if (!key) return "";
    return key.slice(0, 10) + "****" + key.slice(-6);
  };

  const copyKey = async (keyValue) => {
    await navigator.clipboard.writeText(keyValue);
    alert("API key copied!");
  };

  const createKey = async () => {
    try {
      await api.post("/api/keys/generate", newKey);

      setShowCreateModal(false);

      setNewKey({
        email: "yash@test.com",
        name: "",
        dailyQuota: 1000,
      });

      loadDashboard();
    } catch (err) {
      console.error(err);
      alert("Failed to create key");
    }
  };

  const openActionModal = (type, key) => {
    setActionType(type);
    setSelectedKey(key);
    setShowActionModal(true);
  };

  const executeAction = async () => {
    try {
      if (actionType === "toggle") {
        await api.patch(`/api/keys/${selectedKey.id}/toggle`);
      }

      if (actionType === "delete") {
        await api.delete(`/api/keys/${selectedKey.id}`);
      }

      setShowActionModal(false);
      loadDashboard();
    } catch (err) {
      console.error(err);
      alert("Action failed");
    }
  };

  const filteredKeys = keys.filter((key) => {
    const matchesSearch = key.name
      .toLowerCase()
      .includes(searchTerm.toLowerCase());

    const matchesFilter =
      filterStatus === "ALL" ||
      (filterStatus === "ACTIVE" && key.active) ||
      (filterStatus === "DISABLED" && !key.active);

    return matchesSearch && matchesFilter;
  });

  const getSeverityBadge = (severity) => {
    switch (severity) {
      case "CRITICAL":
        return "bg-red-500/20 text-red-300";
      case "HIGH":
        return "bg-orange-500/20 text-orange-300";
      case "MEDIUM":
        return "bg-yellow-500/20 text-yellow-300";
      default:
        return "bg-slate-500/20 text-slate-300";
    }
  };

  return (
    <div className="min-h-screen bg-black text-white">
      <Navbar />

      <div className="p-8">
        {/* SECURITY */}
        <div className="mb-12">
          <p className="text-cyan-400 uppercase tracking-[0.3em] text-sm">
            Security Operations Center
          </p>

          <h1 className="text-5xl font-bold mt-2 mb-8">
            Threat Intelligence Dashboard
          </h1>

          <div className="grid grid-cols-4 gap-6 mb-8">
            <StatCard title="Total Events" value={securityStats.totalEvents} color="cyan" />
            <StatCard title="Critical Alerts" value={securityStats.criticalEvents} color="red" />
            <StatCard title="High Severity" value={securityStats.highEvents} color="orange" />
            <StatCard title="Medium Severity" value={securityStats.mediumEvents} color="yellow" />
          </div>

          <div className="bg-slate-950 border border-red-500/20 rounded-3xl p-8">
            <h2 className="text-3xl font-bold mb-6 text-red-300">
              Recent Threat Activity
            </h2>

            <div className="overflow-x-auto">
              <table className="w-full">
                <thead>
                  <tr className="text-left border-b border-slate-700 text-slate-400">
                    <th className="py-4">Type</th>
                    <th>Severity</th>
                    <th>IP</th>
                    <th>Endpoint</th>
                    <th>Message</th>
                  </tr>
                </thead>

                <tbody>
                  {securityEvents.map((event) => (
                    <tr key={event.id} className="border-b border-slate-800">
                      <td className="py-4 font-semibold">{event.eventType}</td>

                      <td>
                        <span
                          className={`px-3 py-2 rounded-xl font-bold ${getSeverityBadge(
                            event.severity
                          )}`}
                        >
                          {event.severity}
                        </span>
                      </td>

                      <td>{event.ipAddress}</td>
                      <td>{event.endpoint}</td>
                      <td>{event.message}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        </div>

        {/* API VAULT */}
        <div>
          <div className="flex justify-between items-center mb-8">
            <div>
              <p className="text-cyan-400 uppercase tracking-[0.3em] text-sm">
                Access Control Layer
              </p>
              <h1 className="text-5xl font-bold mt-2">
                API Credentials Vault
              </h1>
            </div>

            <div className="flex gap-4">
  <button
    onClick={() => window.location.href = "/playground"}
    className="bg-purple-500 hover:bg-purple-400 px-6 py-3 rounded-xl font-bold"
  >
    API Playground
  </button>

  <button
    onClick={() => setShowCreateModal(true)}
    className="bg-cyan-500 hover:bg-cyan-400 px-6 py-3 rounded-xl font-bold"
  >
    + Create API Key
  </button>
</div>
          </div>

          {/* SEARCH + FILTER */}
          <div className="flex gap-4 mb-8">
            <input
              type="text"
              placeholder="Search API keys..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="flex-1 bg-slate-900 border border-slate-700 rounded-xl p-4"
            />

            <select
              value={filterStatus}
              onChange={(e) => setFilterStatus(e.target.value)}
              className="bg-slate-900 border border-slate-700 rounded-xl px-4"
            >
              <option value="ALL">All</option>
              <option value="ACTIVE">Active</option>
              <option value="DISABLED">Disabled</option>
            </select>
          </div>

          {loading ? (
            <p className="text-slate-400 text-xl">Loading dashboard...</p>
          ) : (
            <>
              <div className="grid grid-cols-4 gap-6 mb-10">
                <StatCard title="Total Keys" value={keys.length} color="cyan" />
                <StatCard title="Active Keys" value={activeKeys} color="green" />
                <StatCard title="Disabled Keys" value={disabledKeys} color="red" />
                <StatCard title="Requests Used" value={totalRequests} color="purple" />
              </div>

              {filteredKeys.length === 0 ? (
                <div className="bg-slate-950 rounded-3xl p-10 text-center text-slate-400">
                  No API keys found.
                </div>
              ) : (
                <div className="space-y-6">
                  {filteredKeys.map((key) => (
                    <div
                      key={key.id}
                      className="bg-slate-900 border border-cyan-500/10 rounded-2xl p-6 flex justify-between items-center"
                    >
                      <div>
                        <p className="text-2xl font-bold">{key.name}</p>

                        <p className="text-cyan-300 text-xl mt-2">
                          {maskKey(key.keyValue)}
                        </p>

                        <div className="flex gap-10 mt-4 text-slate-400">
                          <p>Quota: {key.dailyQuota}</p>
                          <p>Requests Used: {key.requestsUsed}</p>
                        </div>
                      </div>

                      <div className="flex gap-4 items-center">
                        <button
                          onClick={() => copyKey(key.keyValue)}
                          className="bg-cyan-500 hover:bg-cyan-400 px-5 py-3 rounded-xl font-bold"
                        >
                          Copy
                        </button>

                        <button
                          onClick={() => openActionModal("toggle", key)}
                          className={`px-5 py-3 rounded-xl font-bold ${
                            key.active
                              ? "bg-red-500 hover:bg-red-400"
                              : "bg-green-500 hover:bg-green-400"
                          }`}
                        >
                          {key.active ? "Disable" : "Enable"}
                        </button>

                        <button
                          onClick={() => openActionModal("delete", key)}
                          className="bg-slate-700 hover:bg-slate-600 px-5 py-3 rounded-xl font-bold"
                        >
                          Delete
                        </button>
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </>
          )}
        </div>

        {/* CREATE MODAL */}
        {showCreateModal && (
          <Modal
            title="Generate New API Key"
            onCancel={() => setShowCreateModal(false)}
            onConfirm={createKey}
            confirmText="Generate"
          >
            <input
              type="text"
              placeholder="Key Name"
              value={newKey.name}
              onChange={(e) =>
                setNewKey({ ...newKey, name: e.target.value })
              }
              className="w-full mb-4 p-4 bg-slate-900 rounded-xl"
            />

            <input
              type="number"
              value={newKey.dailyQuota}
              onChange={(e) =>
                setNewKey({
                  ...newKey,
                  dailyQuota: parseInt(e.target.value),
                })
              }
              className="w-full p-4 bg-slate-900 rounded-xl"
            />
          </Modal>
        )}

        {/* ACTION MODAL */}
        {showActionModal && (
          <Modal
            title={
              actionType === "delete"
                ? "Delete API Key?"
                : "Change API Key Status?"
            }
            onCancel={() => setShowActionModal(false)}
            onConfirm={executeAction}
            confirmText="Confirm"
          >
            <p className="text-slate-300">
              This action will modify your API credentials.
            </p>
          </Modal>
        )}
      </div>
    </div>
  );
}

function StatCard({ title, value, color }) {
  return (
    <div className={`bg-slate-950 border border-${color}-500/30 rounded-2xl p-6`}>
      <p className="text-slate-400">{title}</p>
      <p className={`text-5xl font-bold text-${color}-300 mt-3`}>
        {value}
      </p>
    </div>
  );
}

function Modal({ title, children, onCancel, onConfirm, confirmText }) {
  return (
    <div className="fixed inset-0 bg-black/80 flex items-center justify-center z-50">
      <div className="bg-slate-950 border border-cyan-500 rounded-2xl p-8 w-[500px]">
        <h2 className="text-3xl font-bold mb-6 text-cyan-300">{title}</h2>

        <div className="mb-6">{children}</div>

        <div className="flex gap-4">
          <button
            onClick={onConfirm}
            className="flex-1 bg-cyan-500 hover:bg-cyan-400 py-4 rounded-xl font-bold"
          >
            {confirmText}
          </button>

          <button
            onClick={onCancel}
            className="flex-1 bg-slate-700 hover:bg-slate-600 py-4 rounded-xl font-bold"
          >
            Cancel
          </button>
        </div>
      </div>
    </div>
  );
}

export default DashboardPage;