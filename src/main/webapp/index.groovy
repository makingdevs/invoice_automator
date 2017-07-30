import javax.servlet.http.HttpServletResponse

response.status = HttpServletResponse.SC_OK
model = [moment : new Date()]
json(model)
