import axios from "axios";

const api = axios.create({
  baseURL: "https://shieldgate-production.up.railway.app",
});

export const getSecurityStats = () => {
  return api.get("/api/security/stats");
};

export const getSecurityEvents = () => {
  return api.get("/api/security/events");
};

export default api;