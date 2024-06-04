import React, { useState } from 'react';
import axios from 'axios';
import Spinner from './Spinner'; 

const SummarizePage: React.FC = () => {
  const [url, setUrl] = useState<string>('');
  const [loading, setLoading] = useState(false)
  const [summary, setSummary] = useState<string>('');

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    try {
      const response = await axios.post(`${process.env.REACT_APP_API_URL}/summarize`, { url });
      setLoading(false);
      setSummary(response.data.summary);
    } catch (error) {
      setLoading(false);
      console.error('Error summarizing URL', error);
      alert('Error summarizing the url!')
    }
  };

  return (
    <div>
      <h1>Summarize a Website</h1>
      <form onSubmit={handleSubmit}>
        <input
          type="text"
          value={url}
          onChange={(e) => setUrl(e.target.value)}
          placeholder="Enter website URL"
        />
        <button type="submit">Summarize</button>
      </form>
      {loading && <Spinner />}
      {summary && (
        <div>
          <h2>Summary</h2>
          <p>{summary}</p>
        </div>
      )}
    </div>
  );
};

export default SummarizePage;
