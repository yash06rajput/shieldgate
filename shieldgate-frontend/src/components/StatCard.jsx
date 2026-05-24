export default function StatCard({ title, value }) {
  return (
    <div className="bg-gray-900 rounded-2xl shadow-lg p-6 border border-gray-800">
      <h3 className="text-gray-400 text-sm">{title}</h3>
      <p className="text-3xl font-bold text-white mt-2">{value}</p>
    </div>
  );
}