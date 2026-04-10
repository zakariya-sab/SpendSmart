"""
SpendSmart ML API — Spending Prediction Service
================================================
This Flask API receives the user's expense history (last 3-6 months)
and predicts the expected spending for the next month using Linear Regression.

Endpoint: POST /predict
Input:
    {
        "expenses": [
            {"month": "2026-01", "amount": 1200.0},
            {"month": "2026-02", "amount": 1350.0},
            {"month": "2026-03", "amount": 1180.0}
        ]
    }
Output:
    {
        "predicted_amount": 1243.5,
        "trend": "decreasing",
        "confidence": 0.85,
        "months_analyzed": 3
    }

Run: python app.py
Server runs on: http://localhost:5000
"""

from flask import Flask, request, jsonify
import numpy as np
from sklearn.linear_model import LinearRegression
import logging

# Initialize Flask application
app = Flask(__name__)

# Configure logging to show useful information
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)


def predict_next_spending(monthly_data):
    """
    Predict next month's spending using Linear Regression.

    Trains a simple linear regression model on the provided monthly spending data
    where X is the month index (0, 1, 2, ...) and Y is the spending amount.
    Then predicts the spending for the next month (index = len(data)).

    Parameters:
        monthly_data (list): List of dicts with 'month' (str) and 'amount' (float) keys.
                             Must have at least 2 data points for regression.

    Returns:
        dict: A dictionary with:
            - predicted_amount (float): The predicted spending for next month
            - trend (str): "increasing", "decreasing", or "stable"
            - confidence (float): R² score of the regression model (0.0 to 1.0)
            - months_analyzed (int): Number of months used for prediction
    """
    if not monthly_data or len(monthly_data) < 2:
        # Not enough data for regression — use the last known amount as prediction
        if monthly_data and len(monthly_data) == 1:
            amount = monthly_data[0].get('amount', 0)
            return {
                'predicted_amount': round(amount, 2),
                'trend': 'stable',
                'confidence': 0.0,
                'months_analyzed': 1
            }
        return {
            'predicted_amount': 0.0,
            'trend': 'stable',
            'confidence': 0.0,
            'months_analyzed': 0
        }

    # Extract the spending amounts as a numpy array
    amounts = np.array([entry.get('amount', 0) for entry in monthly_data], dtype=float)

    # Use month indices (0, 1, 2, ...) as the feature variable X
    months_count = len(amounts)
    X = np.arange(months_count).reshape(-1, 1)
    y = amounts

    # Train a Linear Regression model on the historical data
    model = LinearRegression()
    model.fit(X, y)

    # Predict the next month's spending (index = months_count)
    next_month_index = np.array([[months_count]])
    predicted_amount = float(model.predict(next_month_index)[0])

    # Ensure prediction is not negative (spending can't be negative)
    predicted_amount = max(0.0, predicted_amount)

    # Calculate the model's R² confidence score (0 = no fit, 1 = perfect fit)
    confidence = float(model.score(X, y)) if months_count > 1 else 0.0
    confidence = max(0.0, min(1.0, confidence))

    # Determine the spending trend based on the regression slope
    slope = float(model.coef_[0])
    if abs(slope) < 10:
        # Less than 10 MAD change per month is considered stable
        trend = 'stable'
    elif slope > 0:
        trend = 'increasing'
    else:
        trend = 'decreasing'

    logger.info(
        f"Prediction: {predicted_amount:.2f} | Trend: {trend} | "
        f"Slope: {slope:.2f} | Confidence: {confidence:.2f} | "
        f"Months: {months_count}"
    )

    return {
        'predicted_amount': round(predicted_amount, 2),
        'trend': trend,
        'confidence': round(confidence, 2),
        'months_analyzed': months_count
    }


@app.route('/predict', methods=['POST'])
def predict():
    """
    Main prediction endpoint — receives expense history and returns spending prediction.

    Accepts a JSON body with an 'expenses' array of monthly totals.
    Returns the predicted amount for next month and a trend indicator.

    Returns:
        200: JSON with prediction data
        400: JSON with error message if input is invalid
        500: JSON with error message if prediction fails
    """
    try:
        # Parse and validate the request body
        data = request.get_json()
        if not data:
            return jsonify({'error': 'Request body must be JSON'}), 400

        # Extract the expenses array from the request
        expenses = data.get('expenses', [])
        if not isinstance(expenses, list):
            return jsonify({'error': "'expenses' must be a list"}), 400

        logger.info(f"Received prediction request with {len(expenses)} months of data")

        # Perform the prediction using the ML model
        result = predict_next_spending(expenses)

        return jsonify(result), 200

    except Exception as e:
        logger.error(f"Prediction failed: {str(e)}")
        return jsonify({'error': f'Prediction failed: {str(e)}'}), 500


@app.route('/health', methods=['GET'])
def health_check():
    """
    Health check endpoint for monitoring the Flask service.

    Returns:
        200: JSON confirming the service is running
    """
    return jsonify({
        'status': 'ok',
        'service': 'SpendSmart ML API',
        'version': '1.0.0'
    }), 200


@app.route('/', methods=['GET'])
def index():
    """
    Root endpoint providing API documentation summary.

    Returns:
        200: JSON with available endpoints
    """
    return jsonify({
        'service': 'SpendSmart ML Prediction API',
        'endpoints': {
            'POST /predict': 'Predict next month spending from expense history',
            'GET /health': 'Health check'
        },
        'input_format': {
            'expenses': [
                {'month': 'YYYY-MM', 'amount': 1200.0}
            ]
        }
    }), 200


# Run the Flask development server on port 5000
if __name__ == '__main__':
    logger.info("Starting SpendSmart ML API on http://localhost:5000")
    app.run(host='0.0.0.0', port=5000, debug=True)
