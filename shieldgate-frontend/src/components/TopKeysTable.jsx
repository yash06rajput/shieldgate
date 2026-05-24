export default function TopKeysTable({ keys }) {
  return (
    <div className="bg-gray-900 rounded-2xl p-6 border border-gray-800">
      <h2 className="text-xl font-semibold text-white mb-4">Top API Keys</h2>

      <table className="w-full text-left">
        <thead>
          <tr className="text-gray-400">
            <th>Name</th>
            <th>Requests Used</th>
            <th>Status</th>
          </tr>
        </thead>

        <tbody>
          {keys.map((key) => (
            <tr key={key.id} className="border-t border-gray-800">
              <td className="py-3 text-white">{key.name}</td>
              <td className="text-blue-400">{key.requestsUsed}</td>
              <td>
                <span
                  className={`px-3 py-1 rounded-full text-sm ${
                    key.active
                      ? "bg-green-600 text-white"
                      : "bg-red-600 text-white"
                  }`}
                >
                  {key.active ? "Active" : "Disabled"}
                </span>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}