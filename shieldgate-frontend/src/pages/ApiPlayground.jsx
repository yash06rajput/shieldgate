import { useState } from "react";

const API_URL = "https://shieldgate-production.up.railway.app/api/";

export default function ApiPlayground() {
  const [apiKey, setApiKey] = useState("");
  const [status, setStatus] = useState(null);
  const [loading, setLoading] = useState(false);
  const [singleResult, setSingleResult] = useState("");
  const [stressResults, setStressResults] = useState([]);
  const [summary, setSummary] = useState(null);
  const [cooldown, setCooldown] = useState(0);

  const getStatusConfig = (code) => {
    switch (code) {
      case 200:
        return {
          label: "ACCESS GRANTED",
          color: "bg-emerald-500",
          description: "Valid API key. Request successfully processed.",
        };
      case 401:
        return {
          label: "INVALID KEY",
          color: "bg-red-500",
          description: "API key is invalid or missing.",
        };
      case 403:
        return {
          label: "KEY DISABLED",
          color: "bg-orange-500",
          description: "This API key has been disabled.",
        };
      case 423:
        return {
          label: "TEMP LOCKED",
          color: "bg-pink-500",
          description:
            "Repeated suspicious activity temporarily locked this API key.",
        };
      case 429:
        return {
          label: "RATE LIMITED",
          color: "bg-yellow-400 text-black",
          description:
            "Too many requests detected. Redis rate limiter blocked access.",
        };
      default:
        return {
          label: "UNKNOWN",
          color: "bg-slate-500",
          description: "Unexpected response.",
        };
    }
  };

  const clearPlayground = () => {
    setStatus(null);
    setSingleResult("");
    setStressResults([]);
    setSummary(null);
    setCooldown(0);
  };

  const callApi = async () => {
    if (!apiKey.trim()) return;

    setLoading(true);
    setStressResults([]);
    setSummary(null);

    try {
      const res = await fetch(API_URL, {
        method: "GET",
        headers: {
          "x-api-key": apiKey,
        },
      });

      const text = await res.text();

      setStatus(res.status);
      setSingleResult(text);
    } catch (err) {
      setStatus(500);
      setSingleResult(err.message);
    }

    setLoading(false);
  };

  const stressTest = async () => {
    if (!apiKey.trim()) return;

    clearPlayground();
    setLoading(true);

    let success = 0;
    let blocked = 0;
    let failed = 0;

    const results = [];

    const requests = Array.from({ length: 10 }, async (_, i) => {
      try {
        const res = await fetch(API_URL, {
          method: "GET",
          headers: {
            "x-api-key": apiKey,
          },
        });

        results.push({
          id: i + 1,
          status: res.status,
        });

        if (res.status === 200) success++;
        else if (res.status === 429) blocked++;
        else failed++;
      } catch {
        results.push({
          id: i + 1,
          status: 500,
        });
        failed++;
      }
    });

    await Promise.all(requests);

    results.sort((a, b) => a.id - b.id);

    setStressResults(results);

    setSummary({
      success,
      blocked,
      failed,
    });

    setCooldown(10);

    const timer = setInterval(() => {
      setCooldown((prev) => {
        if (prev <= 1) {
          clearInterval(timer);
          return 0;
        }
        return prev - 1;
      });
    }, 1000);

    setLoading(false);
  };

  const config = status ? getStatusConfig(status) : null;

  return (
    <div className="min-h-screen bg-black text-white px-6 py-10">
      <div className="max-w-6xl mx-auto">
        <div className="bg-slate-950 border border-cyan-500/20 rounded-3xl p-10 shadow-2xl">

          <h1 className="text-5xl font-bold mb-3">
            ShieldGate Security Playground
          </h1>

          <p className="text-slate-400 text-lg mb-10">
            Live interactive API security testing environment demonstrating
            authentication, Redis rate limiting, burst detection, and abuse protection.
          </p>

          <div className="bg-slate-900 rounded-2xl p-8 mb-8">
            <label className="block text-slate-300 font-semibold mb-3">
              API Key
            </label>

            <input
              type="text"
              value={apiKey}
              onChange={(e) => setApiKey(e.target.value)}
              placeholder="Paste API key here..."
              className="w-full p-4 rounded-xl bg-slate-800 border border-slate-700 text-white text-lg"
            />

            <div className="flex gap-4 mt-6 flex-wrap">
              <button
                onClick={callApi}
                disabled={loading}
                className="bg-cyan-500 hover:bg-cyan-400 px-6 py-3 rounded-xl font-bold text-lg"
              >
                {loading ? "Testing..." : "Single Request Test"}
              </button>

              <button
                onClick={stressTest}
                disabled={loading}
                className="bg-red-500 hover:bg-red-400 px-6 py-3 rounded-xl font-bold text-lg"
              >
                Stress Test (10 Parallel Requests)
              </button>

              <button
                onClick={clearPlayground}
                className="bg-slate-700 hover:bg-slate-600 px-6 py-3 rounded-xl font-bold text-lg"
              >
                Reset Playground
              </button>
            </div>
          </div>

          {cooldown > 0 && (
            <div className="mb-8 bg-yellow-900 border border-yellow-600 rounded-2xl p-5">
              <div className="text-yellow-200 font-bold text-xl">
                Redis Window Active
              </div>
              <div className="text-yellow-100 mt-2">
                Rate limiter cooldown in progress. Retry in <b>{cooldown}s</b>
              </div>
            </div>
          )}

          {config && (
            <div className="bg-slate-900 rounded-2xl p-8 mb-8">
              <div
                className={`inline-block px-5 py-2 rounded-xl font-bold text-lg ${config.color}`}
              >
                {config.label}
              </div>

              <div className="mt-5 text-2xl font-semibold">
                HTTP Status: {status}
              </div>

              <div className="mt-2 text-slate-300 text-lg">
                {config.description}
              </div>

              <div className="mt-6 bg-black rounded-xl p-5 text-slate-300 border border-slate-800">
                {singleResult}
              </div>
            </div>
          )}

          {summary && (
            <div className="grid md:grid-cols-3 gap-6 mb-8">
              <div className="bg-emerald-950 rounded-2xl p-8">
                <div className="text-5xl font-bold">{summary.success}</div>
                <div className="text-lg mt-2">Successful Requests</div>
              </div>

              <div className="bg-yellow-950 rounded-2xl p-8">
                <div className="text-5xl font-bold">{summary.blocked}</div>
                <div className="text-lg mt-2">Rate Limited</div>
              </div>

              <div className="bg-red-950 rounded-2xl p-8">
                <div className="text-5xl font-bold">{summary.failed}</div>
                <div className="text-lg mt-2">Failures</div>
              </div>
            </div>
          )}

          {stressResults.length > 0 && (
            <div className="bg-slate-900 rounded-2xl p-8">
              <h2 className="text-3xl font-bold mb-8">
                Stress Test Results
              </h2>

              <div className="grid md:grid-cols-2 gap-4">
                {stressResults.map((result) => {
                  const cfg = getStatusConfig(result.status);

                  return (
                    <div
                      key={result.id}
                      className="bg-black rounded-xl p-5 flex justify-between items-center border border-slate-800"
                    >
                      <span className="text-lg">
                        Request #{result.id}
                      </span>

                      <span
                        className={`px-4 py-2 rounded-xl font-bold text-lg ${cfg.color}`}
                      >
                        {result.status}
                      </span>
                    </div>
                  );
                })}
              </div>
            </div>
          )}

        </div>
      </div>
    </div>
  );
}