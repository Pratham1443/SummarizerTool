import React, { useState, useEffect } from 'react';
import axios from 'axios';

const HistoryPage: React.FC = () => {
  const [history, setHistory] = useState<{ id: number, url: string, summary: string }[]>([]);
  const [editIndex, setEditIndex] = useState(-1);
  const [editSummary, setEditSummary] = useState("");

  useEffect(() => {
    const fetchHistory = async () => {
      try {
        const response = await axios.get(`${process.env.REACT_APP_API_URL}/history`);
        const parsedData = response.data.map((item: string) => JSON.parse(item));
        setHistory(parsedData);
      } catch (error) {
        console.error('Error fetching history', error);
      }
    };
    fetchHistory();
  }, []);

  const handleEdit = (index: number) => {
    setEditIndex(index);
    setEditSummary(history[index].summary);
  };

  const handleSave = async (id: number) => {
    try {
      const response = await axios.put(`${process.env.REACT_APP_API_URL}/history/${id}`, {
        summary: editSummary,
      });
      const newHistory = [...history];
      newHistory[editIndex].summary = editSummary;
      setHistory(newHistory);
      setEditIndex(-1); // Reset edit index
      console.log('Update successful:', response.data);
    } catch (error) {
      console.error('Error updating history', error);
    }
  };

  return (
    <div>
      <h1>Request History</h1>
      {history.length > 0 ? (
        <ul>
          {history.map((item, index) => (
            <li key={item.id}>
              {item.id} - {item.url} - 
              {editIndex === index ? (
                <input
                  type="text"
                  value={editSummary}
                  onChange={(e) => setEditSummary(e.target.value)}
                />
              ) : (
                item.summary
              )}
              {editIndex === index ? (
                <button onClick={() => handleSave(item.id)}>Save</button>
              ) : (
                <button onClick={() => handleEdit(index)}>Edit</button>
              )}
            </li>
          ))}
        </ul>
      ) : (
        <p>No history available</p>
      )}
    </div>
  );
};

export default HistoryPage;
