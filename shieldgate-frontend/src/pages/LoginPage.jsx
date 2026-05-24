import { useNavigate } from "react-router-dom";
import { Shield, Lock, Activity, KeyRound, Eye, EyeOff } from "lucide-react";
import { useState } from "react";

export default function LoginPage() {
  const navigate = useNavigate();
  const [showPassword, setShowPassword] = useState(false);

  const handleLogin = (e) => {
    e.preventDefault();
    navigate("/dashboard");
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-950 via-blue-950 to-black text-white flex">
      
      {/* Left Branding Section */}
      <div className="hidden lg:flex w-1/2 flex-col justify-center px-20 relative overflow-hidden">
        <div className="absolute inset-0 opacity-10">
          <div className="absolute top-20 left-20 w-72 h-72 bg-cyan-500 rounded-full blur-3xl"></div>
          <div className="absolute bottom-20 right-20 w-72 h-72 bg-blue-600 rounded-full blur-3xl"></div>
        </div>

        <div className="relative z-10">
          <div className="flex items-center gap-4 mb-8">
            <Shield className="w-12 h-12 text-cyan-400" />
            <div>
              <h1 className="text-4xl font-bold">ShieldGate</h1>
              <p className="text-slate-400">Enterprise API Security Platform</p>
            </div>
          </div>

          <h2 className="text-5xl font-bold leading-tight mb-6">
            Secure API Access.
            <br />
            Monitor Threats.
            <br />
            Protect Infrastructure.
          </h2>

          <p className="text-slate-300 text-lg mb-10 max-w-xl">
            Centralized API security operations platform for authentication,
            API key governance, threat detection, and access control monitoring.
          </p>

          <div className="grid grid-cols-2 gap-6 max-w-xl">
            <FeatureCard
              icon={<Lock />}
              title="JWT Protected"
              subtitle="Token-based secure authentication"
            />
            <FeatureCard
              icon={<Activity />}
              title="Threat Monitoring"
              subtitle="Track suspicious API behavior"
            />
            <FeatureCard
              icon={<KeyRound />}
              title="Access Control"
              subtitle="API key lifecycle management"
            />
            <FeatureCard
              icon={<Shield />}
              title="SOC Ready"
              subtitle="Security event visibility"
            />
          </div>
        </div>
      </div>

      {/* Right Login Section */}
      <div className="w-full lg:w-1/2 flex items-center justify-center px-6">
        <div className="w-full max-w-md bg-slate-900/80 backdrop-blur-xl border border-cyan-500/20 rounded-3xl shadow-2xl p-10">
          <div className="text-center mb-8">
            <Shield className="w-14 h-14 text-cyan-400 mx-auto mb-4" />
            <h2 className="text-3xl font-bold">Vendor Portal Login</h2>
            <p className="text-slate-400 mt-2">
              Access your API security command center
            </p>
          </div>

          <form onSubmit={handleLogin} className="space-y-5">
            <div>
              <label className="block text-sm mb-2 text-slate-300">Email</label>
              <input
                type="email"
                placeholder="vendor@company.com"
                className="w-full p-4 rounded-xl bg-slate-800 border border-slate-700 focus:border-cyan-400 outline-none"
              />
            </div>

            <div>
              <label className="block text-sm mb-2 text-slate-300">Password</label>
              <div className="relative">
                <input
                  type={showPassword ? "text" : "password"}
                  placeholder="Enter password"
                  className="w-full p-4 rounded-xl bg-slate-800 border border-slate-700 focus:border-cyan-400 outline-none pr-14"
                />
                <button
                  type="button"
                  onClick={() => setShowPassword(!showPassword)}
                  className="absolute right-4 top-4 text-slate-400"
                >
                  {showPassword ? <EyeOff size={22} /> : <Eye size={22} />}
                </button>
              </div>
            </div>

            <div className="flex justify-between items-center text-sm">
              <label className="flex items-center gap-2 text-slate-300">
                <input type="checkbox" />
                Remember me
              </label>

              <button
                type="button"
                className="text-cyan-400 hover:text-cyan-300"
              >
                Forgot password?
              </button>
            </div>

            <button
              type="submit"
              className="w-full bg-cyan-500 hover:bg-cyan-400 transition py-4 rounded-xl font-semibold text-lg"
            >
              Sign In Securely
            </button>
          </form>

          <div className="mt-8 text-center text-sm text-slate-500">
            Protected by ShieldGate Security Infrastructure
          </div>
        </div>
      </div>
    </div>
  );
}

function FeatureCard({ icon, title, subtitle }) {
  return (
    <div className="bg-slate-900/70 border border-slate-800 rounded-2xl p-5">
      <div className="text-cyan-400 mb-3">{icon}</div>
      <h3 className="font-semibold">{title}</h3>
      <p className="text-slate-400 text-sm mt-1">{subtitle}</p>
    </div>
  );
}