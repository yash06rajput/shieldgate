import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:8080",
});

export const getSecurityStats = () => {
  return api.get("/api/security/stats");
};

export const getSecurityEvents = () => {
  return api.get("/api/security/events");
};

export default api;