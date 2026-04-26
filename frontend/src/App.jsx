import { useEffect, useState } from 'react'

function App() {
  const [books, setBooks] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  useEffect(() => {
    fetch('http://localhost:8080/api/books')
      .then(res => res.json())
      .then(data => setBooks(data))
      .catch(() => setError('Could not load books. Is the backend running?'))
      .finally(() => setLoading(false))
  }, [])

  return (
    <div style={{ maxWidth: 600, margin: '40px auto', fontFamily: 'sans-serif', padding: '0 16px' }}>
      <h1>Books</h1>

      {loading && <p>Loading...</p>}
      {error && <p style={{ color: 'red' }}>{error}</p>}

      {books.map(book => (
        <div key={book.id} style={{ borderBottom: '1px solid #eee', padding: '12px 0' }}>
          <strong>{book.title}</strong> — {book.author}
          {book.publishedYear && <span style={{ color: '#888', marginLeft: 8 }}>({book.publishedYear})</span>}
        </div>
      ))}
    </div>
  )
}

export default App
