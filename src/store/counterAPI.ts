export function fetchCount(currentCount = 0, amount = 1) {
    return new Promise<{ data: number }>(resolve =>
        setTimeout(() => resolve({ data: currentCount + amount }), 1000),
    );
}