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
  const [showModal, setShowModal] = useState(false);

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

  const toggleKey = async (id) => {
    try {
      await api.patch(`/api/keys/${id}/toggle`);
      fetchKeys();
    } catch (err) {
      console.error(err);
    }
  };

  const copyKey = (keyValue) => {
    navigator.clipboard.writeText(keyValue);
    alert("API Key copied!");
  };

  const createKey = async () => {
    try {
      await api.post("/api/keys/generate", newKey);

      setShowModal(false);

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

        {/* SECURITY ANALYTICS */}
        <div className="mb-12">
          <p className="text-cyan-400 uppercase tracking-[0.3em] text-sm">
            Security Operations Center
          </p>

          <h1 className="text-5xl font-bold mt-2 mb-8">
            Threat Intelligence Dashboard
          </h1>

          <div className="grid grid-cols-4 gap-6 mb-8">
            <div className="bg-slate-950 border border-cyan-500/30 rounded-2xl p-6 shadow-lg">
              <p className="text-slate-400">Total Security Events</p>
              <p className="text-5xl font-bold text-cyan-300 mt-3">
                {securityStats.totalEvents}
              </p>
            </div>

            <div className="bg-slate-950 border border-red-500/30 rounded-2xl p-6 shadow-lg">
              <p className="text-slate-400">Critical Alerts</p>
              <p className="text-5xl font-bold text-red-300 mt-3">
                {securityStats.criticalEvents}
              </p>
            </div>

            <div className="bg-slate-950 border border-orange-500/30 rounded-2xl p-6 shadow-lg">
              <p className="text-slate-400">High Severity</p>
              <p className="text-5xl font-bold text-orange-300 mt-3">
                {securityStats.highEvents}
              </p>
            </div>

            <div className="bg-slate-950 border border-yellow-500/30 rounded-2xl p-6 shadow-lg">
              <p className="text-slate-400">Medium Severity</p>
              <p className="text-5xl font-bold text-yellow-300 mt-3">
                {securityStats.mediumEvents}
              </p>
            </div>
          </div>

          {/* Recent Threat Events */}
          <div className="bg-slate-950 border border-red-500/20 rounded-3xl p-8 mb-10">
            <h2 className="text-3xl font-bold mb-6 text-red-300">
              Recent Threat Activity
            </h2>

            <div className="overflow-x-auto">
              <table className="w-full text-left">
                <thead>
                  <tr className="border-b border-slate-700 text-slate-400">
                    <th className="py-4">Type</th>
                    <th>Severity</th>
                    <th>IP Address</th>
                    <th>Endpoint</th>
                    <th>Message</th>
                  </tr>
                </thead>

                <tbody>
                  {securityEvents.length === 0 ? (
                    <tr>
                      <td colSpan="5" className="py-6 text-slate-500">
                        No threat activity detected
                      </td>
                    </tr>
                  ) : (
                    securityEvents.map((event) => (
                      <tr
                        key={event.id}
                        className="border-b border-slate-800 hover:bg-slate-900"
                      >
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
                    ))
                  )}
                </tbody>
              </table>
            </div>
          </div>
        </div>

        {/* API KEY DASHBOARD */}
        <div>
          <div className="mb-8 flex justify-between items-center">
            <div>
              <p className="text-cyan-400 uppercase tracking-[0.3em] text-sm">
                Access Control Layer
              </p>

              <h1 className="text-5xl font-bold mt-2">
                API Credentials Vault
              </h1>
            </div>

            <button
              onClick={() => setShowModal(true)}
              className="bg-cyan-500 hover:bg-cyan-400 px-6 py-3 rounded-xl font-bold"
            >
              + Create API Key
            </button>
          </div>

          {loading ? (
            <p>Loading dashboard...</p>
          ) : (
            <>
              <div className="grid grid-cols-4 gap-6 mb-10">
                <div className="bg-slate-950 border border-cyan-500/30 rounded-2xl p-6">
                  <p className="text-slate-400">Total Keys</p>
                  <p className="text-5xl font-bold text-cyan-300 mt-3">
                    {keys.length}
                  </p>
                </div>

                <div className="bg-slate-950 border border-green-500/30 rounded-2xl p-6">
                  <p className="text-slate-400">Active Keys</p>
                  <p className="text-5xl font-bold text-green-300 mt-3">
                    {activeKeys}
                  </p>
                </div>

                <div className="bg-slate-950 border border-red-500/30 rounded-2xl p-6">
                  <p className="text-slate-400">Disabled Keys</p>
                  <p className="text-5xl font-bold text-red-300 mt-3">
                    {disabledKeys}
                  </p>
                </div>

                <div className="bg-slate-950 border border-purple-500/30 rounded-2xl p-6">
                  <p className="text-slate-400">Requests Used</p>
                  <p className="text-5xl font-bold text-purple-300 mt-3">
                    {totalRequests}
                  </p>
                </div>
              </div>

              <div className="space-y-6">
                {keys.map((key) => (
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
                        className="bg-cyan-500 hover:bg-cyan-400 px-6 py-3 rounded-xl font-bold"
                      >
                        Copy
                      </button>

                      <button
                        onClick={() => toggleKey(key.id)}
                        className={`px-6 py-3 rounded-xl font-bold ${
                          key.active
                            ? "bg-red-500 hover:bg-red-400"
                            : "bg-green-500 hover:bg-green-400"
                        }`}
                      >
                        {key.active ? "Disable" : "Enable"}
                      </button>

                      <span
                        className={`px-6 py-3 rounded-xl font-bold ${
                          key.active
                            ? "bg-green-500/20 text-green-300"
                            : "bg-red-500/20 text-red-300"
                        }`}
                      >
                        {key.active ? "ACTIVE" : "DISABLED"}
                      </span>
                    </div>
                  </div>
                ))}
              </div>
            </>
          )}
        </div>

        {/* MODAL */}
        {showModal && (
          <div className="fixed inset-0 bg-black/80 flex items-center justify-center">
            <div className="bg-slate-950 border border-cyan-500 rounded-2xl p-8 w-[500px]">
              <h2 className="text-3xl font-bold mb-6 text-cyan-300">
                Generate New API Key
              </h2>

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
                placeholder="Daily Quota"
                value={newKey.dailyQuota}
                onChange={(e) =>
                  setNewKey({
                    ...newKey,
                    dailyQuota: parseInt(e.target.value),
                  })
                }
                className="w-full mb-6 p-4 bg-slate-900 rounded-xl"
              />

              <div className="flex gap-4">
                <button
                  onClick={createKey}
                  className="flex-1 bg-cyan-500 hover:bg-cyan-400 py-4 rounded-xl font-bold"
                >
                  Generate
                </button>

                <button
                  onClick={() => setShowModal(false)}
                  className="flex-1 bg-slate-700 hover:bg-slate-600 py-4 rounded-xl font-bold"
                >
                  Cancel
                </button>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}

export default DashboardPage;