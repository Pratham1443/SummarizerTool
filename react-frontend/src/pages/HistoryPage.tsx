import React, { useState, useEffect } from 'react';
import axios from 'axios';

const HistoryPage: React.FC = () => {
  const [history, setHistory] = useState<{ url: string, summary: string }[]>([]);

  useEffect(() => {
    const fetchHistory = async () => {
      try {
        const response = await axios.get(`${process.env.REACT_APP_API_URL}/history`);
        setHistory(response.data);
      } catch (error) {
        console.error('Error fetching history', error);
      }
    };
    fetchHistory();
  }, []);

  return (
    <div>
      <h1>Request History</h1>
      {history.length > 0 ? (
        <ul>
          {history.map((item, index) => (
            <li key={index}>{item.url} - {item.summary}</li>
          ))}
        </ul>
      ) : (
        <p>No history available</p>
      )}
    </div>
  );
};

export default HistoryPage;
