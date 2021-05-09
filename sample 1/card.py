class Card:
    """Card class that represents a playing card"""
    def __init__(self, suit, value):
        self.suit = suit
        self.value = value

    def __str__(self):
        return f"{self.value} {self.suit}"